package com.amedigital.wallet.service.impl.order.release;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.release.purchase.AuthorizedPurchaseReleaseState;
import com.amedigital.wallet.service.state.order.release.purchase.CreatedPurchaseReleaseState;
import com.amedigital.wallet.service.state.order.release.purchase.ReleasedPurchaseReleaseState;
import com.amedigital.wallet.service.state.order.release.transferbetweenwallets.AuthorizedTransferBetweenWalletsReleaseState;
import com.amedigital.wallet.service.state.order.release.transferbetweenwallets.CreatedTransferBetweenWalletsReleaseState;
import com.amedigital.wallet.service.state.order.release.transferbetweenwallets.ReleasedTransferBetweenWalletsReleaseState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReleasePurchaseOrderStateService implements SecondaryOrderStateService<PurchaseOrder, ReleaseOrder> {

    private final Map<OrderStatus, SecondaryOrderState> states = new HashMap<>();
    private final WalletRepository walletRepository;

    @Autowired
    public ReleasePurchaseOrderStateService(WalletRepository walletRepository,
                                                         CreatedPurchaseReleaseState createdPurchaseReleaseState,
                                                         AuthorizedPurchaseReleaseState authorizedPurchaseReleaseState,
                                                         ReleasedPurchaseReleaseState releasedPurchaseReleaseState
    ) {

        states.put(OrderStatus.CREATED, createdPurchaseReleaseState);
        states.put(OrderStatus.AUTHORIZED, authorizedPurchaseReleaseState);
        states.put(OrderStatus.RELEASED,  releasedPurchaseReleaseState);

        this.walletRepository = walletRepository;
    }
    @Override
    public Mono<ReleaseOrder> create(PurchaseOrder purchaseOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).create(purchaseOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> authorize(PurchaseOrder purchaseOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).authorize(purchaseOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> finish(PurchaseOrder purchaseOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).finish(purchaseOrder, releaseOrder);
    }
	@Override
	public Mono<ReleaseOrder> cancel(PurchaseOrder purchaseOrder, ReleaseOrder releaseOrder) {
		  return states.get(releaseOrder.getStatus()).cancel(purchaseOrder, releaseOrder);
	}

}
