package com.amedigital.wallet.service.impl.order.release;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.model.order.primary.StoreCashInOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.release.storecashin.AuthorizedStoreCashInReleaseState;
import com.amedigital.wallet.service.state.order.release.storecashin.CreatedStoreCashInReleaseState;
import com.amedigital.wallet.service.state.order.release.storecashin.ReleasedStoreCashInReleaseState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.EnumMap;
import java.util.Map;

@Service
public class ReleaseStoreCashInOrderStateService implements SecondaryOrderStateService<StoreCashInOrder, ReleaseOrder> {


    private final Map<OrderStatus, SecondaryOrderState> states = new EnumMap<>(OrderStatus.class);

    @Autowired
    public ReleaseStoreCashInOrderStateService(CreatedStoreCashInReleaseState createdStoreCashInOrderReleaseState,
                                               AuthorizedStoreCashInReleaseState authorizedStoreCashInOrderReleaseState,
                                               ReleasedStoreCashInReleaseState releasedStoreCashInOrderReleaseState
    ) {

        states.put(OrderStatus.CREATED, createdStoreCashInOrderReleaseState);
        states.put(OrderStatus.AUTHORIZED, authorizedStoreCashInOrderReleaseState);
        states.put(OrderStatus.RELEASED, releasedStoreCashInOrderReleaseState);

    }

    @Override
    public Mono<ReleaseOrder> create(StoreCashInOrder storeCashInOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).create(storeCashInOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> authorize(StoreCashInOrder storeCashInOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).authorize(storeCashInOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> finish(StoreCashInOrder storeCashInOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).finish(storeCashInOrder, releaseOrder);
    }

	@Override
	public Mono<ReleaseOrder> cancel(StoreCashInOrder storeCashInOrder, ReleaseOrder releaseOrder) {
		return states.get(releaseOrder.getStatus()).cancel(storeCashInOrder, releaseOrder);
	}

}
