package com.amedigital.wallet.service.impl.order.release;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.model.order.primary.CashInOrder;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.release.cashin.AuthorizedCashInReleaseState;
import com.amedigital.wallet.service.state.order.release.cashin.CreatedCashInReleaseState;
import com.amedigital.wallet.service.state.order.release.cashin.ReleasedCashInReleaseState;
import com.amedigital.wallet.service.state.order.release.transferbetweenwallets.AuthorizedTransferBetweenWalletsReleaseState;
import com.amedigital.wallet.service.state.order.release.transferbetweenwallets.CreatedTransferBetweenWalletsReleaseState;
import com.amedigital.wallet.service.state.order.release.transferbetweenwallets.ReleasedTransferBetweenWalletsReleaseState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReleaseCashInOrderStateService implements SecondaryOrderStateService<CashInOrder, ReleaseOrder> {



    private final Map<OrderStatus, SecondaryOrderState> states = new HashMap<>();
    private final WalletRepository walletRepository;

    @Autowired
    public ReleaseCashInOrderStateService(WalletRepository walletRepository,
                                                         CreatedCashInReleaseState createdCashInReleaseState,
                                                         AuthorizedCashInReleaseState authorizedCashInReleaseState,
                                                         ReleasedCashInReleaseState releasedCashInReleaseState
    ) {

        states.put(OrderStatus.CREATED, createdCashInReleaseState);
        states.put(OrderStatus.AUTHORIZED, authorizedCashInReleaseState);
        states.put(OrderStatus.RELEASED,  releasedCashInReleaseState);

        this.walletRepository = walletRepository;
    }


    @Override
    public Mono<ReleaseOrder> create(CashInOrder cashInOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).create(cashInOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> authorize(CashInOrder cashInOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).authorize(cashInOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> finish(CashInOrder cashInOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).finish(cashInOrder, releaseOrder);
    }


	@Override
	public Mono<ReleaseOrder> cancel(CashInOrder cashInOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).cancel(cashInOrder, releaseOrder);
	}

}
