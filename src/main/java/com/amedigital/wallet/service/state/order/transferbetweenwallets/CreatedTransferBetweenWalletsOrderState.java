package com.amedigital.wallet.service.state.order.transferbetweenwallets;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
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

import static com.amedigital.wallet.constants.Constants.TRANSFER_BETWEEN_WALLETS_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.createdMessageException;
import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.TransactionType.DEBIT;
import static java.util.UUID.randomUUID;

@Service
public class CreatedTransferBetweenWalletsOrderState implements OrderState<TransferBetweenWalletsOrder> {

    private final OrderRepository repository;
    private final PaymentMethodRouter paymentMethodRouter;
    private final DynamoRepository dynamoRepository;
    private final WalletRepository walletRepository;
    private final BalanceRouter router;

    private static final Logger LOG = LoggerFactory.getLogger(CreatedTransferBetweenWalletsOrderState.class);

    @Autowired
    public CreatedTransferBetweenWalletsOrderState(OrderRepository repository,
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
    public Mono<TransferBetweenWalletsOrder> create(TransferBetweenWalletsOrder order) {
        ZonedDateTime now = ZonedDateTime.now();

        Action action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(now)
                .build();

        //crio as transações de debito...
        List<Transaction> transactionList = order.getTransactions()
                .stream()
                .map(t -> t.copy()
                        .setUuid(randomUUID().toString())
                        .setStatus(TransactionStatus.CREATED)
                        .setLatest(true)
                        .setPeerWalletId(order.getToWalletId()) // seto qual a wallet que vai receber a transferencia.
                        .setType(DEBIT)
                        .build())
                .map(t -> (Transaction) t)
                .collect(Collectors.toList());

        var orderDetailUuid = UUID.randomUUID().toString();

        TransferBetweenWalletsOrder orderCreate = order.copy()
                .setStatus(CREATED)
                .setTransactions(transactionList)
                .setAction(action)
                .setOrderDetailUuid(orderDetailUuid)
                .build();

        LOG.info("Salvando transferencia entre wallets [{}] com o status [{}] ...", orderCreate.getUuid(), CREATED);

        return repository
                .save(orderCreate)
                .cast(TransferBetweenWalletsOrder.class)
                .flatMap(this::authorize)
                .flatMap(tw -> dynamoRepository.save(orderDetailUuid, order.getCustomPayload()).map(t -> tw));
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> authorize(TransferBetweenWalletsOrder order) {
        var transactions = processWithBalance(order).switchIfEmpty(Flux.fromStream(order.getTransactions().stream())
                .flatMap(paymentMethodRouter::authorize));

        return transactions
                .collectList()
                .map(li -> setOrderStatus(li, order))
                .flatMap(repository::save)
                .cast(TransferBetweenWalletsOrder.class);
    }


    private Flux<Transaction> processWithBalance(TransferBetweenWalletsOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(t -> router.route(t.getPaymentMethod(), order))
                .flatMap($ -> Flux.fromStream(order.getTransactions()
                        .stream())
                        .map(t2 -> t2.copy().setStatus(TransactionStatus.DENIED).build())
                        .map(Transaction.class::cast));
    }


    @Override
    public Mono<TransferBetweenWalletsOrder> capture(TransferBetweenWalletsOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                createdMessageException("capturar", TRANSFER_BETWEEN_WALLETS_ORDER_TYPE));
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> cancel(TransferBetweenWalletsOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                createdMessageException("cancelar", TRANSFER_BETWEEN_WALLETS_ORDER_TYPE));
    }

    private TransferBetweenWalletsOrder setOrderStatus(List<Transaction> transactions, TransferBetweenWalletsOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.AUTHORIZE)
                .setParentId(order.getAction().getId())
                .build();

        TransferBetweenWalletsOrder purchaseOrder = order.copy()
                .setStatus(OrderStatus.AUTHORIZED)
                .setTransactions(transactions)
                .setAction(action)
                .setCreatedAt(order.getCreatedAt())
                .build();

        return purchaseOrder.getTransactions()
                .stream()
                .filter(t -> TransactionStatus.DENIED.equals(t.getStatus()) || TransactionStatus.ERROR.equals(t.getStatus()))
                .findFirst()
                .map(transaction -> purchaseOrder.copy().setStatus(OrderStatus.DENIED).build())
                .orElse(purchaseOrder);
    }
}
