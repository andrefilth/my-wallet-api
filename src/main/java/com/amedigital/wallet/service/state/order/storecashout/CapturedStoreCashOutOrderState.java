package com.amedigital.wallet.service.state.order.storecashout;

import static com.amedigital.wallet.constants.Constants.STORE_CASH_OUT_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.capturedMessageException;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.OrderState;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CapturedStoreCashOutOrderState implements OrderState<StoreCashOutOrder> {

    private final PaymentMethodRouter paymentMethodRouter;
    private final OrderRepository repository;

    @Autowired
    public CapturedStoreCashOutOrderState(PaymentMethodRouter paymentMethodRouter, OrderRepository repository) {
        this.paymentMethodRouter = paymentMethodRouter;
        this.repository = repository;
    }

    @Override
    public Mono<StoreCashOutOrder> create(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("criar", STORE_CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashOutOrder> authorize(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("autorizar", STORE_CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashOutOrder> capture(StoreCashOutOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<StoreCashOutOrder> cancel(StoreCashOutOrder order) {
        return Flux.fromIterable(order.getTransactions())
                .flatMap(paymentMethodRouter::cancel)
                .collectList()
                .map(transactions -> setOrderStatus(transactions, order))
                .flatMap(repository::save)
                .cast(StoreCashOutOrder.class);
    }

    private StoreCashOutOrder setOrderStatus(List<Transaction> transactions, StoreCashOutOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.CANCEL)
                .setParentId(order.getAction().getId())
                .build();

        StoreCashOutOrder.Builder builder = order.copy()
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
