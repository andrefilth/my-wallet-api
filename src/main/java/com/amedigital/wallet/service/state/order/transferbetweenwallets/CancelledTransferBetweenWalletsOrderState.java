package com.amedigital.wallet.service.state.order.transferbetweenwallets;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.TRANSFER_BETWEEN_WALLETS_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.cancelledMessageException;

@Service
public class CancelledTransferBetweenWalletsOrderState implements OrderState<TransferBetweenWalletsOrder> {

    @Override
    public Mono<TransferBetweenWalletsOrder> create(TransferBetweenWalletsOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("criar", TRANSFER_BETWEEN_WALLETS_ORDER_TYPE));
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> authorize(TransferBetweenWalletsOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("autorizar", TRANSFER_BETWEEN_WALLETS_ORDER_TYPE));
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> capture(TransferBetweenWalletsOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("capturar", TRANSFER_BETWEEN_WALLETS_ORDER_TYPE));
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> cancel(TransferBetweenWalletsOrder order) {
        return Mono.just(order);
    }

}