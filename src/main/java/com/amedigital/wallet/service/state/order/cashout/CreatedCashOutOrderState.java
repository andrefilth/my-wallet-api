package com.amedigital.wallet.service.state.order.cashout;

import static com.amedigital.wallet.constants.Constants.CASH_OUT_ORDER_TYPE;
import static com.amedigital.wallet.constants.Constants.FAST_CASH_MANAGER_WALLET_ID;
import static com.amedigital.wallet.constants.Messages.createdMessageException;
import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.TransactionType.CREDIT;
import static com.amedigital.wallet.constants.enuns.TransactionType.DEBIT;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.BankTransferStatus;
import com.amedigital.wallet.constants.enuns.CashStatus;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.CashOutOrder;
import com.amedigital.wallet.model.transaction.BankTransferTransaction;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.DynamoRepository;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.OrderState;
import com.google.common.collect.Lists;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreatedCashOutOrderState implements OrderState<CashOutOrder> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CreatedCashOutOrderState.class);
	
    private final OrderRepository repository;
    private final PaymentMethodRouter paymentMethodRouter;
    private final DynamoRepository dynamoRepository;

    @Autowired
    public CreatedCashOutOrderState(OrderRepository repository,
                                   PaymentMethodRouter paymentMethodRouter,
                                   DynamoRepository dynamoRepository) {

        this.repository = repository;
        this.paymentMethodRouter = paymentMethodRouter;
        this.dynamoRepository = dynamoRepository;
    }

	@Override
	public Mono<CashOutOrder> create(CashOutOrder order) {
        
        BankTransferTransaction bankTransferTransaction = (BankTransferTransaction) order.getTransactions().get(0);
        var now = ZonedDateTime.now();
        
        var debit = bankTransferTransaction
        		.copy()
        		.setUuid(UUID.randomUUID().toString())
        		.setPeerWalletId(order.getCreatedByWalletId())
        		.setWalletId(order.getCreatedByWalletId())
        		.setAmountInCents(order.getTotalAmountInCents())
        		.setOrderUuid(order.getUuid())
        		.setStatus(TransactionStatus.CREATED)
        		.setBankTransferStatus(BankTransferStatus.CREATED)
        		.setType(DEBIT)
        		.setCreatedAt(now)
        		.setUpdatedAt(now)
        		.build();
        
        var debitWallet = CashTransaction
                .builder()
                .setUuid(UUID.randomUUID().toString())
                .setPeerWalletId(order.getCreatedByWalletId())
                .setWalletId(order.getCreatedByWalletId())
                .setAmountInCents(order.getTotalAmountInCents())
                .setCashStatus(CashStatus.CREATED)
                .setCashUpdatedAt(now)
                .setCashCreatedAt(now)
                .setOrderUuid(order.getUuid())
                .setStatus(TransactionStatus.CREATED)
                .setType(DEBIT)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();
        
        var credit = CashTransaction
                .builder()
                .setUuid(UUID.randomUUID().toString())
                .setPeerWalletId(FAST_CASH_MANAGER_WALLET_ID)
                .setWalletId(FAST_CASH_MANAGER_WALLET_ID)
                .setAmountInCents(order.getTotalAmountInCents())
                .setCashStatus(CashStatus.CREATED)
                .setCashUpdatedAt(now)
                .setCashCreatedAt(now)
                .setOrderUuid(order.getUuid())
                .setStatus(TransactionStatus.CREATED)
                .setType(CREDIT)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();
        
        var transactions = Lists.newArrayList(debit, debitWallet, credit).stream().map(t -> (Transaction) t ).collect(Collectors.toList());

        
        Action action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(now)
                .build();
        
        CashOutOrder orderCreate = order.copy()
        		.setStatus(OrderStatus.CREATED)
        		.setTransactions(transactions)
        		.setAction(action)
        		.setOrderDetailUuid(UUID.randomUUID().toString())
        		.build();

        LOG.info("Salvando transferencia CashOut [{}] com o status [{}] ...", orderCreate.getUuid(), OrderStatus.CREATED);

        return repository
                .save(orderCreate)
                .cast(CashOutOrder.class)
                .flatMap(this::authorize)
                .flatMap(tw -> dynamoRepository.save(orderCreate.getOrderDetailUuid(), order.getCustomPayload()).map(t -> tw));
	}

	@Override
	public Mono<CashOutOrder> authorize(CashOutOrder order) {
		
        var transactions = Flux.fromStream(order.getTransactions().stream())
                .flatMap(paymentMethodRouter::authorize);

			return transactions
				.collectList()
				.map(li -> setOrderStatus(li, order))
				.flatMap(repository::save)
				.cast(CashOutOrder.class);
	}

	@Override
	public Mono<CashOutOrder> capture(CashOutOrder order) {
		throw new AmeInvalidInputException("wallet_validation",
                createdMessageException("capturar", CASH_OUT_ORDER_TYPE));
	}

	@Override
	public Mono<CashOutOrder> cancel(CashOutOrder order) {
		throw new AmeInvalidInputException("wallet_validation",
				createdMessageException("cancelar", CASH_OUT_ORDER_TYPE));
	}

    private CashOutOrder setOrderStatus(List<Transaction> transactions, CashOutOrder order) {
    	
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.AUTHORIZE)
                .setParentId(order.getAction().getId())
                .build();

        CashOutOrder cashOutOrder = order.copy()
                .setStatus(OrderStatus.AUTHORIZED)
                .setTransactions(transactions)
                .setAction(action)
                .build();

        return cashOutOrder.getTransactions()
                .stream()
                .filter(t -> TransactionStatus.AUTHORIZED != t.getStatus())
                .findFirst()
                .map(transaction -> {
                    var transactions1 = cashOutOrder.getTransactions()
                            .stream()
                            .map(t -> {
                                if (PaymentMethod.CASH == t.getPaymentMethod()) {
                                    var cashTransaction = (CashTransaction) t;

                                    return cashTransaction.copy()
                                            .setCashStatus(CashStatus.DENIED)
                                            .setStatus(TransactionStatus.DENIED)
                                            .build();
                                }

                                return t;
                            })
                            .collect(Collectors.toList());

                    return cashOutOrder.copy().setTransactions(transactions1).setStatus(OrderStatus.DENIED).build();
                })
                .orElse(cashOutOrder);
    }
}
