package com.amedigital.wallet.service.impl.order.release;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.release.transferbetweenwallets.AuthorizedTransferBetweenWalletsReleaseState;
import com.amedigital.wallet.service.state.order.release.transferbetweenwallets.CreatedTransferBetweenWalletsReleaseState;
import com.amedigital.wallet.service.state.order.release.transferbetweenwallets.ReleasedTransferBetweenWalletsReleaseState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReleaseTransferBetweenWalletOrderStateService implements SecondaryOrderStateService<TransferBetweenWalletsOrder, ReleaseOrder> {


    private final Map<OrderStatus, SecondaryOrderState> states = new HashMap<>();
    private final WalletRepository walletRepository;

    @Autowired
    public ReleaseTransferBetweenWalletOrderStateService(WalletRepository walletRepository,
                                                         CreatedTransferBetweenWalletsReleaseState createdTransferBetweenWalletsReleaseState,
                                                         AuthorizedTransferBetweenWalletsReleaseState authorizedTransferBetweenWalletsReleaseState,
                                                         ReleasedTransferBetweenWalletsReleaseState releasedTransferBetweenWalletsReleaseState
                                                         ) {

        states.put(OrderStatus.CREATED, createdTransferBetweenWalletsReleaseState);
        states.put(OrderStatus.AUTHORIZED, authorizedTransferBetweenWalletsReleaseState);
        states.put(OrderStatus.RELEASED,  releasedTransferBetweenWalletsReleaseState);

        this.walletRepository = walletRepository;
    }

    @Override
    public Mono<ReleaseOrder> create(TransferBetweenWalletsOrder transferBetweenWalletsOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).create(transferBetweenWalletsOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> authorize(TransferBetweenWalletsOrder transferBetweenWalletsOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).authorize(transferBetweenWalletsOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> finish(TransferBetweenWalletsOrder transferBetweenWalletsOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).finish(transferBetweenWalletsOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> cancel(TransferBetweenWalletsOrder transferBetweenWalletsOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).cancel(transferBetweenWalletsOrder, releaseOrder);
    }

}
