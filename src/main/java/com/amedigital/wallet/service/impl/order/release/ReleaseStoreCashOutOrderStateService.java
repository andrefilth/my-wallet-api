package com.amedigital.wallet.service.impl.order.release;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.release.storecashout.AuthorizedStoreCashOutReleaseState;
import com.amedigital.wallet.service.state.order.release.storecashout.CreatedStoreCashOutReleaseState;
import com.amedigital.wallet.service.state.order.release.storecashout.ReleasedStoreCashOutReleaseState;

import reactor.core.publisher.Mono;

@Service
public class ReleaseStoreCashOutOrderStateService implements SecondaryOrderStateService<StoreCashOutOrder, ReleaseOrder> {


    private final Map<OrderStatus, SecondaryOrderState<StoreCashOutOrder, ReleaseOrder>> states = new EnumMap<>(OrderStatus.class);

    @Autowired
    public ReleaseStoreCashOutOrderStateService(CreatedStoreCashOutReleaseState createdStoreCashOutOrderReleaseState,
                                               AuthorizedStoreCashOutReleaseState authorizedStoreCashOutOrderReleaseState,
                                               ReleasedStoreCashOutReleaseState releasedStoreCashOutOrderReleaseState) {

        states.put(OrderStatus.CREATED, createdStoreCashOutOrderReleaseState);
        states.put(OrderStatus.AUTHORIZED, authorizedStoreCashOutOrderReleaseState);
        states.put(OrderStatus.RELEASED, releasedStoreCashOutOrderReleaseState);
    }

    @Override
    public Mono<ReleaseOrder> create(StoreCashOutOrder storeCashOutOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).create(storeCashOutOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> authorize(StoreCashOutOrder storeCashOutOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).authorize(storeCashOutOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> finish(StoreCashOutOrder storeCashOutOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).finish(storeCashOutOrder, releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> cancel(StoreCashOutOrder storeCashOutOrder, ReleaseOrder releaseOrder) {
        return states.get(releaseOrder.getStatus()).cancel(storeCashOutOrder, releaseOrder);
    }
}
