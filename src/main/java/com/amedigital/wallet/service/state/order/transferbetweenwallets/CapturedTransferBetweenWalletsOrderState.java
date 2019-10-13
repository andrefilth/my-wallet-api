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
import static com.amedigital.wallet.constants.Messages.capturedMessageException;

@Service
public class CapturedTransferBetweenWalletsOrderState implements OrderState<TransferBetweenWalletsOrder> {

    private final PaymentMethodRouter paymentMethodRouter;
    private final OrderRepository repository;

    @Autowired
    public CapturedTransferBetweenWalletsOrderState(PaymentMethodRouter paymentMethodRouter, OrderRepository repository) {
        this.paymentMethodRouter = paymentMethodRouter;
        this.repository = repository;
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> create(TransferBetweenWalletsOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("criar", TRANSFER_BETWEEN_WALLETS_ORDER_TYPE));
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> authorize(TransferBetweenWalletsOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("autorizar", TRANSFER_BETWEEN_WALLETS_ORDER_TYPE));
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> capture(TransferBetweenWalletsOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> cancel(TransferBetweenWalletsOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(paymentMethodRouter::cancel)
                .collectList()
                .map(transactions -> setOrderStatus(transactions, order))
                .flatMap(repository::save)
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
