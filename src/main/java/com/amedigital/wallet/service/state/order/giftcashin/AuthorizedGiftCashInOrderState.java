package com.amedigital.wallet.service.state.order.giftcashin;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.GiftCashInOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.amedigital.wallet.constants.Constants.GIFT_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.authorizedMessageException;

@Service
public class AuthorizedGiftCashInOrderState implements OrderState<GiftCashInOrder> {

    private final PaymentMethodRouter router;
    private final OrderRepository orderRepository;

    public AuthorizedGiftCashInOrderState(final PaymentMethodRouter router, final OrderRepository orderRepository) {
        this.router = router;
        this.orderRepository = orderRepository;
    }

    @Override
    public Mono<GiftCashInOrder> create(final GiftCashInOrder order) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                authorizedMessageException("criar", GIFT_CASH_IN_ORDER_TYPE)));
    }

    @Override
    public Mono<GiftCashInOrder> authorize(final GiftCashInOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<GiftCashInOrder> capture(final GiftCashInOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(router::capture)
                .collectList()
                .map(trs -> order.copy()
                        .setStatus(allCapturedOrAuthorizedStatus(trs))
                        .setTransactions(trs)
                        .setAction(captureActionFor(order))
                        .build())
                .flatMap(orderRepository::save)
                .cast(GiftCashInOrder.class);
    }

    private Action actionForOrderAndType(GiftCashInOrder order, ActionType actionType) {
        return new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(actionType)
                .setParentId(order.getAction().getId())
                .build();
    }

    private boolean allMatchsStatus(TransactionStatus transactionStatus, List<Transaction> transactions) {
        return transactions.stream()
                .allMatch(t -> transactionStatus == t.getStatus());
    }

    private OrderStatus allCapturedOrAuthorizedStatus(List<Transaction> transactions) {
        return allMatchsStatus(TransactionStatus.CAPTURED, transactions)
                ? OrderStatus.CAPTURED
                : OrderStatus.AUTHORIZED;
    }

    private Action captureActionFor(GiftCashInOrder order) {
        return actionForOrderAndType(order, ActionType.CAPTURE);
    }

    private Action cancelledActionFor(GiftCashInOrder order) {
        return actionForOrderAndType(order, ActionType.CANCEL);
    }

    @Override
    public Mono<GiftCashInOrder> cancel(final GiftCashInOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(router::cancel)
                .collectList()
                .map(transactions -> setOrderStatus(transactions, order))
                .flatMap(orderRepository::save)
                .cast(GiftCashInOrder.class);
    }

    private GiftCashInOrder setOrderStatus(List<Transaction> transactions, GiftCashInOrder order) {
        return order.copy()
                .setTransactions(transactions)
                .setAction(cancelledActionFor(order))
                .setCreatedAt(order.getCreatedAt())
                .setStatus(
                        allMatchsStatus(TransactionStatus.CANCELLED, transactions)
                                ? OrderStatus.CANCELLED
                                : order.getStatus())
                .build();

    }
}
