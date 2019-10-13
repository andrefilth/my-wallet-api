package com.amedigital.wallet.service.state;

import com.amedigital.wallet.model.order.Order;
import reactor.core.publisher.Mono;

public interface OrderState<T extends Order> {

    Mono<T> create(T order);

    Mono<T> authorize(T order);

    Mono<T> capture(T order);

    Mono<T> cancel(T order);

}