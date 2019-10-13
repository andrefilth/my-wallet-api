package com.amedigital.wallet.service.state;

import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import reactor.core.publisher.Mono;

public interface ReleaseState {

    Mono<ReleaseOrder> create(Order order, ReleaseOrder releaseOrder);

    Mono<ReleaseOrder> authorize(Order order, ReleaseOrder releaseOrder);

    Mono<ReleaseOrder> release(Order order, ReleaseOrder releaseOrder);
}
