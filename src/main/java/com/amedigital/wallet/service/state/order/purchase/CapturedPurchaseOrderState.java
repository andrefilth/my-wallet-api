package com.amedigital.wallet.service.state.order.purchase;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
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

import static com.amedigital.wallet.constants.Constants.PURCHASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.capturedMessageException;

@Service
public class CapturedPurchaseOrderState implements OrderState<PurchaseOrder> {

    private final PaymentMethodRouter paymentMethodRouter;
    private final OrderRepository repository;

    @Autowired
    public CapturedPurchaseOrderState(PaymentMethodRouter paymentMethodRouter, OrderRepository repository) {
        this.paymentMethodRouter = paymentMethodRouter;
        this.repository = repository;
    }

    @Override
    public Mono<PurchaseOrder> create(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("criar", PURCHASE_ORDER_TYPE));
    }

    @Override
    public Mono<PurchaseOrder> authorize(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("autorizar", PURCHASE_ORDER_TYPE));
    }

    @Override
    public Mono<PurchaseOrder> capture(PurchaseOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<PurchaseOrder> cancel(PurchaseOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(paymentMethodRouter::cancel)
                .collectList()
                .map(transactions -> setOrderStatus(transactions, order))
                .flatMap(repository::save)
                .map(o -> (PurchaseOrder) o);
    }

    private PurchaseOrder setOrderStatus(List<Transaction> transactions, PurchaseOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.CANCEL)
                .setParentId(order.getAction().getId())
                .build();

        PurchaseOrder.Builder builder = order.copy()
                .setTransactions(transactions)
                .setAction(action);

        //verifico se todas as transações foram canceladas para poder setar a ordem como cancelada.
        var cancelledTransactions = transactions
                .stream()
                .allMatch(t -> TransactionStatus.CANCELLED.equals(t.getStatus()));

        if (cancelledTransactions) {
            builder.setStatus(OrderStatus.CANCELLED);
        }

        return builder.build();
    }
}
