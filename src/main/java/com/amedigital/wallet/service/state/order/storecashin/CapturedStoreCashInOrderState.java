package com.amedigital.wallet.service.state.order.storecashin;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.StoreCashInOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.amedigital.wallet.constants.Constants.STORE_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.capturedMessageException;

@Service
public class CapturedStoreCashInOrderState implements OrderState<StoreCashInOrder> {

    private final PaymentMethodRouter paymentMethodRouter;
    private final OrderRepository repository;

    @Autowired
    public CapturedStoreCashInOrderState(PaymentMethodRouter paymentMethodRouter, OrderRepository repository) {
        this.paymentMethodRouter = paymentMethodRouter;
        this.repository = repository;
    }

    @Override
    public Mono<StoreCashInOrder> create(StoreCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("criar", STORE_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashInOrder> authorize(StoreCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("autorizar", STORE_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashInOrder> capture(StoreCashInOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<StoreCashInOrder> cancel(StoreCashInOrder order) {
        return Flux.fromIterable(order.getTransactions())
                .flatMap(paymentMethodRouter::cancel)
                .collectList()
                .map(transactions -> setOrderStatus(transactions, order))
                .flatMap(repository::save)
                .cast(StoreCashInOrder.class);
    }

    private StoreCashInOrder setOrderStatus(List<Transaction> transactions, StoreCashInOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.CANCEL)
                .setParentId(order.getAction().getId())
                .build();

        StoreCashInOrder.Builder builder = order.copy()
                .setTransactions(transactions)
                .setAction(action);

        var cancelledTransactions = transactions
                .stream()
                .allMatch(t -> TransactionStatus.CANCELLED.equals(t.getStatus()));

        if (cancelledTransactions) {
            builder.setStatus(OrderStatus.CANCELLED);
        }

        return builder.build();
    }
}
