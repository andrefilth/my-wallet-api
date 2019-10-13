package com.amedigital.wallet.service.state.order.storecashin;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.StoreCashInOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.DynamoRepository;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.OrderState;
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

import static com.amedigital.wallet.constants.Constants.STORE_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.createdMessageException;
import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.TransactionType.DEBIT;
import static java.util.UUID.randomUUID;

@Service
public class CreatedStoreCashInOrderState implements OrderState<StoreCashInOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(CreatedStoreCashInOrderState.class);
    private final OrderRepository repository;
    private final PaymentMethodRouter paymentMethodRouter;
    private final DynamoRepository dynamoRepository;

    @Autowired
    public CreatedStoreCashInOrderState(OrderRepository repository,
                                        PaymentMethodRouter paymentMethodRouter,
                                        DynamoRepository dynamoRepository) {
        this.repository = repository;
        this.paymentMethodRouter = paymentMethodRouter;
        this.dynamoRepository = dynamoRepository;
    }

    @Override
    public Mono<StoreCashInOrder> create(StoreCashInOrder order) {
        ZonedDateTime now = ZonedDateTime.now();

        Action action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(now)
                .build();

        List<Transaction> transactions = order.getTransactions()
                .stream()
                .map(transaction -> transaction.copy()
                        .setUuid(randomUUID().toString())
                        .setStatus(TransactionStatus.CREATED)
                        .setPeerWalletId(order.getCreditWalletId())
                        .setLatest(true)
                        .setType(DEBIT)
                        .build())
                .map(Transaction.class::cast)
                .collect(Collectors.toList());

        var orderDetailUuid = UUID.randomUUID().toString();

        StoreCashInOrder orderCreate = order.copy()
                .setStatus(CREATED)
                .setTransactions(transactions)
                .setAction(action)
                .setOrderDetailUuid(orderDetailUuid)
                .build();

        LOG.info("Salvando cashin entre wallets [{}] com o status [{}] ...", orderCreate.getUuid(), CREATED);

        return repository
                .save(orderCreate)
                .cast(StoreCashInOrder.class)
                .flatMap(this::authorize)
                .flatMap(tw -> dynamoRepository.save(orderDetailUuid, order.getCustomPayload()).map(t -> tw));
    }

    @Override
    public Mono<StoreCashInOrder> authorize(StoreCashInOrder order) {
        return Flux.fromIterable(order.getTransactions())
                .flatMap(paymentMethodRouter::authorize)
                .collectList()
                .map(li -> setOrderStatus(li, order))
                .flatMap(repository::save)
                .cast(StoreCashInOrder.class);
    }

    @Override
    public Mono<StoreCashInOrder> capture(StoreCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                createdMessageException("capturar", STORE_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashInOrder> cancel(StoreCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                createdMessageException("cancelar", STORE_CASH_IN_ORDER_TYPE));
    }

    private StoreCashInOrder setOrderStatus(List<Transaction> transactions, StoreCashInOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.AUTHORIZE)
                .setParentId(order.getAction().getId())
                .build();

        StoreCashInOrder storeCashInOrder = order.copy()
                .setStatus(OrderStatus.AUTHORIZED)
                .setTransactions(transactions)
                .setAction(action)
                .build();

        return storeCashInOrder.getTransactions()
                .stream()
                .filter(t -> TransactionStatus.DENIED.equals(t.getStatus()) || TransactionStatus.ERROR.equals(t.getStatus()))
                .findFirst()
                .map(transaction -> storeCashInOrder.copy().setStatus(OrderStatus.DENIED).build())
                .orElse(storeCashInOrder);
    }
}
