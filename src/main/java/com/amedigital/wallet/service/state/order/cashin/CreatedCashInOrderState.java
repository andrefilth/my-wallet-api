package com.amedigital.wallet.service.state.order.cashin;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.CashInOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.DynamoRepository;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.order.purchase.CreatedPurchaseOrderState;
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

import static com.amedigital.wallet.constants.Constants.CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.createdMessageException;
import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.TransactionType.DEBIT;
import static java.util.UUID.randomUUID;

@Service
public class CreatedCashInOrderState implements OrderState<CashInOrder> {

    private final OrderRepository repository;
    private final PaymentMethodRouter paymentMethodRouter;
    private final DynamoRepository dynamoRepository;

    private static final Logger LOG = LoggerFactory.getLogger(CreatedPurchaseOrderState.class);

    @Autowired
    public CreatedCashInOrderState(OrderRepository repository,
                                   PaymentMethodRouter paymentMethodRouter,
                                   DynamoRepository dynamoRepository) {

        this.repository = repository;
        this.paymentMethodRouter = paymentMethodRouter;
        this.dynamoRepository = dynamoRepository;
    }

    @Override
    public Mono<CashInOrder> create(CashInOrder order) {
        ZonedDateTime now = ZonedDateTime.now();

        Action action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(now)
                .build();

        List<Transaction> transactionList = order.getTransactions()
                .stream()
                .map(t -> t.copy()
                        .setUuid(randomUUID().toString())
                        .setStatus(TransactionStatus.CREATED)
                        .setLatest(true)
                        .setType(DEBIT)
                        .setPeerWalletId(t.getWalletId()) // seto igual a wallet id, pois quem vai receber é a proporia carteira.
                        .build())
                .map(t -> (Transaction) t)
                .collect(Collectors.toList());

        var orderDetailUuid = UUID.randomUUID().toString();

        CashInOrder orderCreate = order.copy()
                .setStatus(CREATED)
                .setTransactions(transactionList)
                .setAction(action)
                .setOrderDetailUuid(orderDetailUuid)
                .build();

        LOG.info("Salvando order [{}] com o status [{}] ...", orderCreate.getUuid(), CREATED);

        return repository
                .save(orderCreate)
                .map(o -> (CashInOrder) o)
                .flatMap(this::authorize)
                .flatMap(co -> dynamoRepository.save(orderDetailUuid, orderCreate.getCustomPayload()).map(t -> co));
    }

    @Override
    public Mono<CashInOrder> authorize(CashInOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(paymentMethodRouter::authorize)
                .collectList()
                .map(li -> setOrderStatus(li, order))
                .flatMap(repository::save)
                .map(o -> (CashInOrder) o);
    }

    @Override
    public Mono<CashInOrder> capture(CashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                createdMessageException("capturar", CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<CashInOrder> cancel(CashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                createdMessageException("cancelar", CASH_IN_ORDER_TYPE));
    }

    private CashInOrder setOrderStatus(List<Transaction> transactions, CashInOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.AUTHORIZE)
                .setParentId(order.getAction().getId())
                .build();

        CashInOrder cashInOrder = order.copy()
                .setStatus(OrderStatus.AUTHORIZED)
                .setTransactions(transactions)
                .setAction(action)
                .setCreatedAt(order.getCreatedAt())
                .build();

        return cashInOrder.getTransactions()
                .stream()
                .filter(t -> TransactionStatus.DENIED.equals(t.getStatus()) || TransactionStatus.ERROR.equals(t.getStatus()))
                .findFirst()
                .map(transaction -> cashInOrder.copy().setStatus(OrderStatus.DENIED).build())
                .orElse(cashInOrder);
    }
}
