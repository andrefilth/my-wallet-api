package com.amedigital.wallet.service.state.order.purchase;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
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
import static com.amedigital.wallet.constants.Messages.authorizedMessageException;

@Service
public class AuthorizedPurchaseOrderState implements OrderState<PurchaseOrder> {

    private final PaymentMethodRouter router;
    private final OrderRepository orderRepository;

    @Autowired
    public AuthorizedPurchaseOrderState(PaymentMethodRouter router, OrderRepository orderRepository) {
        this.router = router;
        this.orderRepository = orderRepository;
    }

    @Override
    public Mono<PurchaseOrder> create(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                authorizedMessageException("criar", PURCHASE_ORDER_TYPE));
    }

    @Override
    public Mono<PurchaseOrder> authorize(PurchaseOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<PurchaseOrder> capture(PurchaseOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(router::capture)
                .collectList()
                .map(trs -> {
                    var status = (trs.stream().allMatch(e -> e.getStatus().equals(TransactionStatus.CAPTURED))) ? OrderStatus.CAPTURED : OrderStatus.AUTHORIZED;
                    var newOrder = new Action.Builder().setParentId(order.getAction().getId()).setType(ActionType.CAPTURE).setCreatedAt(ZonedDateTime.now()).build();
                    return (Order) order.copy().setStatus(status).setTransactions(trs).setAction(newOrder).setCreatedAt(order.getCreatedAt()).build();
                }).flatMap(orderRepository::save)
                .map(o -> (PurchaseOrder) o);

    }

    @Override
    public Mono<PurchaseOrder> cancel(PurchaseOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(router::cancel)
                .collectList()
                .map(transactions -> setOrderStatus(transactions, order))
                .flatMap(orderRepository::save)
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
