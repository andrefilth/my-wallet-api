package com.amedigital.wallet.service;

import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import reactor.core.publisher.Mono;

public interface SecondaryOrderStateService<T extends Order, K extends SecondaryOrder> {

    Mono<K> create(T t, K k);

    Mono<K> authorize(T t, K k);

    Mono<K> finish(T t, K k);

    Mono<K> cancel(T t, K k);


}
