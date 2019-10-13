package com.amedigital.wallet.service.state.order.purchase;

import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.transaction.CashBackTransaction;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.DynamoRepository;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.strategy.BalanceRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.amedigital.wallet.constants.Constants.PURCHASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.createdMessageException;
import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.TransactionType.DEBIT;
import static java.util.UUID.randomUUID;

@Service
public class CreatedPurchaseOrderState implements OrderState<PurchaseOrder> {

    private final OrderRepository repository;
    private final PaymentMethodRouter paymentMethodRouter;
    private final DynamoRepository dynamoRepository;
    private final WalletRepository walletRepository;
    private final BalanceRouter router;


    private static final Logger LOG = LoggerFactory.getLogger(CreatedPurchaseOrderState.class);

    @Autowired
    public CreatedPurchaseOrderState(OrderRepository repository,
                                     PaymentMethodRouter paymentMethodRouter,
                                     DynamoRepository dynamoRepository,
                                     WalletRepository walletRepository,
                                     BalanceRouter router) {

        this.repository = repository;
        this.paymentMethodRouter = paymentMethodRouter;
        this.dynamoRepository = dynamoRepository;
        this.walletRepository = walletRepository;
        this.router = router;
    }

    @Override
    public Mono<PurchaseOrder> create(PurchaseOrder order) {
        Action action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(ZonedDateTime.now())
                .build();

        List<Transaction> transactionList = order.getTransactions()
                .stream()
                .map(t -> t.copy()
                        .setUuid(randomUUID().toString())
                        .setStatus(TransactionStatus.CREATED)
                        .setLatest(true)
                        .setType(DEBIT)
                        .setOrderUuid(order.getUuid())
                        .setPeerWalletId(order.getCreatedByWalletId()) //seto o id do merchant, pois ele que vai receber.
                        .build())
                .map(t -> (Transaction) t)
                .collect(Collectors.toList());

        var orderDetailUuid = UUID.randomUUID().toString();

        PurchaseOrder orderCreate = order.copy()
                .setStatus(CREATED)
                .setTransactions(transactionList)
                .setAction(action)
                .setOrderDetailUuid(orderDetailUuid)
                .build();

        LOG.info("Salvando order [{}] com o status [{}] ...", orderCreate.getUuid(), CREATED);

        return repository.save(orderCreate)
                .map(o -> (PurchaseOrder) o)
                .flatMap(this::authorize)
                .flatMap(po -> dynamoRepository.save(orderDetailUuid, orderCreate.getCustomPayload()).map(t -> po));
    }

    @Override
    public Mono<PurchaseOrder> authorize(PurchaseOrder order) {
        return processWithBalance(order)
                .switchIfEmpty(Flux.fromStream(order.getTransactions().stream()).flatMap(paymentMethodRouter::authorize))
                .collectList()
                .map(transactions1 -> setOrderStatus(transactions1, order))
                .flatMap(repository::save)
                .cast(PurchaseOrder.class);
    }

    private Flux<Transaction> processWithBalance(PurchaseOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(t -> router.route(t.getPaymentMethod(), order))
                .filter(transaction -> TransactionStatus.DENIED == transaction.getStatus())
                .flatMap($ -> Flux.fromStream(order.getTransactions()
                        .stream())
                        .map(t2 -> t2.copy().setStatus(TransactionStatus.DENIED).build())
                        .map(Transaction.class::cast));
    }

    @Override
    public Mono<PurchaseOrder> capture(PurchaseOrder order) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                createdMessageException("capturar", PURCHASE_ORDER_TYPE)));
    }

    @Override
    public Mono<PurchaseOrder> cancel(PurchaseOrder order) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                createdMessageException("cancelar", PURCHASE_ORDER_TYPE)));
    }

    private PurchaseOrder setOrderStatus(List<Transaction> transactions, PurchaseOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.AUTHORIZE)
                .setParentId(order.getAction().getId())
                .build();

        PurchaseOrder purchaseOrder = order.copy()
                .setStatus(OrderStatus.AUTHORIZED)
                .setTransactions(transactions)
                .setAction(action)
                .setCreatedAt(order.getCreatedAt())
                .build();

        return purchaseOrder.getTransactions()
                .stream()
                .filter(t -> TransactionStatus.AUTHORIZED != t.getStatus())
                .findFirst()
                .map(transaction -> {
                    var transactions1 = purchaseOrder.getTransactions()
                            .stream()
                            .map(t -> {
                                switch (t.getPaymentMethod()) {
                                    case CASH:
                                        var cashTransaction = (CashTransaction) t;

                                        return cashTransaction.copy()
                                                .setCashStatus(CashStatus.DENIED)
                                                .setStatus(TransactionStatus.DENIED)
                                                .build();
                                    case CASH_BACK:
                                        var cashBackTransaction = (CashBackTransaction) t;

                                        return cashBackTransaction.copy()
                                                .setCashStatus(CashBackStatus.DENIED)
                                                .setStatus(TransactionStatus.DENIED)
                                                .build();
                                    default:
                                        return t;
                                }
                            })
                            .collect(Collectors.toList());

                    return purchaseOrder.copy().setTransactions(transactions1).setStatus(OrderStatus.DENIED).build();
                })
                .orElse(purchaseOrder);
    }
}