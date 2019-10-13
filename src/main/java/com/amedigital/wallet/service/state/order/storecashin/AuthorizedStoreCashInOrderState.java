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

import static com.amedigital.wallet.constants.Constants.STORE_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.authorizedMessageException;

@Service
public class AuthorizedStoreCashInOrderState implements OrderState<StoreCashInOrder> {

    private final PaymentMethodRouter router;
    private final OrderRepository orderRepository;

    @Autowired
    public AuthorizedStoreCashInOrderState(PaymentMethodRouter router, OrderRepository orderRepository) {
        this.router = router;
        this.orderRepository = orderRepository;
    }

    @Override
    public Mono<StoreCashInOrder> create(StoreCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                authorizedMessageException("criar", STORE_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashInOrder> authorize(StoreCashInOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<StoreCashInOrder> capture(StoreCashInOrder order) {
        return Flux.fromIterable(order.getTransactions())
                .flatMap(router::capture)
                .collectList()
                .map(transactions -> {
                    var status = transactions.stream().allMatch(e -> e.getStatus().equals(TransactionStatus.CAPTURED))
                            ? OrderStatus.CAPTURED
                            : OrderStatus.AUTHORIZED;

                    var newAction = new Action.Builder()
                            .setParentId(order.getAction().getId())
                            .setType(ActionType.CAPTURE)
                            .setCreatedAt(ZonedDateTime.now())
                            .build();

                    return order.copy()
                            .setStatus(status)
                            .setTransactions(transactions)
                            .setAction(newAction)
                            .build();
                })
                .flatMap(orderRepository::save)
                .cast(StoreCashInOrder.class);
    }

    @Override
    public Mono<StoreCashInOrder> cancel(StoreCashInOrder order) {
        return Flux.fromIterable(order.getTransactions())
                .flatMap(router::cancel)
                .collectList()
                .map(transactions -> setOrderStatus(transactions, order))
                .flatMap(orderRepository::save)
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
