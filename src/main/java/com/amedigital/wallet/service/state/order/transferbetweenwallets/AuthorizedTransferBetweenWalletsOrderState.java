package com.amedigital.wallet.service.state.order.transferbetweenwallets;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
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

import static com.amedigital.wallet.constants.Constants.TRANSFER_BETWEEN_WALLETS_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.authorizedMessageException;

@Service
public class AuthorizedTransferBetweenWalletsOrderState implements OrderState<TransferBetweenWalletsOrder> {

    private final PaymentMethodRouter router;
    private final OrderRepository orderRepository;

    @Autowired
    public AuthorizedTransferBetweenWalletsOrderState(PaymentMethodRouter router, OrderRepository orderRepository) {
        this.router = router;
        this.orderRepository = orderRepository;
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> create(TransferBetweenWalletsOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                authorizedMessageException("criar", TRANSFER_BETWEEN_WALLETS_ORDER_TYPE));
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> authorize(TransferBetweenWalletsOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> capture(TransferBetweenWalletsOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(router::capture)
                .collectList()
                .map(transactions -> {
                    var areAllTransactionsCaptured = transactions.stream().allMatch(e -> e.getStatus().equals(TransactionStatus.CAPTURED));

                    var status = areAllTransactionsCaptured
                            ? OrderStatus.CAPTURED
                            : OrderStatus.AUTHORIZED;

                    var newAction = new Action.Builder()
                            .setParentId(order.getAction().getId())
                            .setType(ActionType.CAPTURE)
                            .setCreatedAt(ZonedDateTime.now())
                            .build();

                    return order.copy().setStatus(status).setTransactions(transactions).setAction(newAction).build();
                }).flatMap(orderRepository::save)
                .cast(TransferBetweenWalletsOrder.class);
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> cancel(TransferBetweenWalletsOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(router::cancel)
                .collectList()
                .map(transactions -> setOrderStatus(transactions, order))
                .flatMap(orderRepository::save)
                .cast(TransferBetweenWalletsOrder.class);
    }

    private TransferBetweenWalletsOrder setOrderStatus(List<Transaction> transactions, TransferBetweenWalletsOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.CANCEL)
                .setParentId(order.getAction().getId())
                .build();

        TransferBetweenWalletsOrder.Builder builder = order.copy()
                .setTransactions(transactions)
                .setCreatedAt(order.getCreatedAt())
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
