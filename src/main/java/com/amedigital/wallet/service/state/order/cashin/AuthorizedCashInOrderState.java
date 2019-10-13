package com.amedigital.wallet.service.state.order.cashin;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.CashInOrder;
import com.amedigital.wallet.model.order.Order;
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

import static com.amedigital.wallet.constants.Constants.CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.authorizedMessageException;

@Service
public class AuthorizedCashInOrderState implements OrderState<CashInOrder> {

    private final PaymentMethodRouter router;
    private final OrderRepository orderRepository;

    @Autowired
    public AuthorizedCashInOrderState(PaymentMethodRouter router, OrderRepository orderRepository) {
        this.router = router;
        this.orderRepository = orderRepository;
    }

    @Override
    public Mono<CashInOrder> create(CashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                authorizedMessageException("criar", CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<CashInOrder> authorize(CashInOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<CashInOrder> capture(CashInOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(router::capture)
                .collectList()
                .map(trs -> {
                    var status = (trs.stream().allMatch(e -> e.getStatus().equals(TransactionStatus.CAPTURED))) ? OrderStatus.CAPTURED : OrderStatus.AUTHORIZED;
                    var newOrder = new Action.Builder().setParentId(order.getAction().getId()).setType(ActionType.CAPTURE).setCreatedAt(ZonedDateTime.now()).build();
                    return (Order) order.copy().setStatus(status).setTransactions(trs).setAction(newOrder).build();
                })
                .flatMap(orderRepository::save)
                .map(o -> (CashInOrder) o);
    }

    @Override
    public Mono<CashInOrder> cancel(CashInOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(router::cancel)
                .collectList()
                .map(t -> setOrderStatus(t, order))
                .flatMap(orderRepository::save)
                .map(o -> (CashInOrder) o);

    }

    private CashInOrder setOrderStatus(List<Transaction> transactions, CashInOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.CANCEL)
                .setParentId(order.getAction().getId())
                .build();

        CashInOrder.Builder builder = order.copy()
                .setTransactions(transactions)
                .setCreatedAt(order.getCreatedAt())
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
