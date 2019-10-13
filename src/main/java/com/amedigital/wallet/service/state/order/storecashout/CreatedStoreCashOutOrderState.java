package com.amedigital.wallet.service.state.order.storecashout;

import static com.amedigital.wallet.constants.Constants.STORE_CASH_OUT_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.createdMessageException;
import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.TransactionType.CREDIT;
import static com.amedigital.wallet.constants.enuns.TransactionType.DEBIT;
import static java.util.UUID.randomUUID;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
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
public class CreatedStoreCashOutOrderState implements OrderState<StoreCashOutOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(CreatedStoreCashOutOrderState.class);
    private final OrderRepository repository;
    private final PaymentMethodRouter paymentMethodRouter;
    private final DynamoRepository dynamoRepository;

    @Autowired
    public CreatedStoreCashOutOrderState(OrderRepository repository,
                                        PaymentMethodRouter paymentMethodRouter,
                                        DynamoRepository dynamoRepository) {
        this.repository = repository;
        this.paymentMethodRouter = paymentMethodRouter;
        this.dynamoRepository = dynamoRepository;
    }

    @Override
    public Mono<StoreCashOutOrder> create(StoreCashOutOrder order) {
    	
    	CashTransaction cashTransactionTemplate = (CashTransaction) order.getTransactions().get(0);
        
        var debitCustomer = cashTransactionTemplate
        		.copy()
        		.setUuid(randomUUID().toString())
                .setWalletId(order.getDebitWalletId())
                .setStatus(TransactionStatus.CREATED)
                .setPeerWalletId(order.getCreditWalletId())
                .setType(DEBIT)
                .build();
        
        var creditStore = cashTransactionTemplate
        		.copy()
        		.setUuid(randomUUID().toString())
                .setWalletId(order.getCreditWalletId())
                .setStatus(TransactionStatus.CREATED)
                .setPeerWalletId(order.getDebitWalletId())
                .setType(CREDIT)
                .build();
        
        var transactions = Lists.newArrayList(debitCustomer, creditStore).stream().map(t -> (Transaction) t ).collect(Collectors.toList());

        ZonedDateTime now = ZonedDateTime.now();
        
        Action action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(now)
                .build();
        
        StoreCashOutOrder orderCreate = order.copy()
                .setStatus(CREATED)
                .setTransactions(transactions)
                .setAction(action)
                .setOrderDetailUuid(randomUUID().toString())
                .build();

        LOG.info("Salvando STORE_CASH_OUT Order [{}] com o status [{}] ...", orderCreate.getUuid(), CREATED);

        return repository
                .save(orderCreate)
                .cast(StoreCashOutOrder.class)
                .flatMap(this::authorize)
                .flatMap(tw -> dynamoRepository.save(orderCreate.getOrderDetailUuid(), order.getCustomPayload()).map(t -> tw));
    }

    @Override
    public Mono<StoreCashOutOrder> authorize(StoreCashOutOrder order) {
        return Flux.fromIterable(order.getTransactions())
                .flatMap(paymentMethodRouter::authorize)
                .collectList()
                .map(li -> setOrderStatus(li, order))
                .flatMap(repository::save)
                .cast(StoreCashOutOrder.class);
    }

    @Override
    public Mono<StoreCashOutOrder> capture(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                createdMessageException("capturar", STORE_CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashOutOrder> cancel(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                createdMessageException("cancelar", STORE_CASH_OUT_ORDER_TYPE));
    }

    private StoreCashOutOrder setOrderStatus(List<Transaction> transactions, StoreCashOutOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.AUTHORIZE)
                .setParentId(order.getAction().getId())
                .build();

        StoreCashOutOrder storeCashOutOrder = order.copy()
                .setStatus(OrderStatus.AUTHORIZED)
                .setTransactions(transactions)
                .setAction(action)
                .build();

        return storeCashOutOrder.getTransactions()
                .stream()
                .filter(t -> TransactionStatus.DENIED.equals(t.getStatus()) || TransactionStatus.ERROR.equals(t.getStatus()))
                .findFirst()
                .map(transaction -> storeCashOutOrder.copy().setStatus(OrderStatus.DENIED).build())
                .orElse(storeCashOutOrder);
    }
}
