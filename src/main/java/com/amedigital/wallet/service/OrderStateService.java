package com.amedigital.wallet.service;

import com.amedigital.wallet.model.order.Order;
import reactor.core.publisher.Mono;

public interface OrderStateService<T extends Order> {

    Mono<T> create(T t);

    Mono<T> authorize(T t);

    Mono<T> capture(T t);

    Mono<T> cancel(T t);

}