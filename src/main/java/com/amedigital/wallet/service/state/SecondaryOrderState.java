package com.amedigital.wallet.service.state;

import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import reactor.core.publisher.Mono;

public interface SecondaryOrderState  <T extends Order, K extends SecondaryOrder> {

    Mono<K> create(T order, K secondaryOrder);

    Mono<K> authorize(T order, K secondaryOrder);

    Mono<K> finish(T order, K secondaryOrder);
    
    Mono<K> cancel(T order, K secondaryOrder);

}