package com.amedigital.wallet.service.strategy;

import com.amedigital.wallet.model.order.Order;
import reactor.core.publisher.Mono;

public interface BalanceStrategy<T> {

    Mono<T> check(Order order);
}
