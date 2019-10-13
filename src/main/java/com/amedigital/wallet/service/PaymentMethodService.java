package com.amedigital.wallet.service;

import reactor.core.publisher.Mono;

public interface PaymentMethodService<T> {

    Mono<T> authorize(T transaction);

    Mono<T> capture(T transaction);

    Mono<T> cancel(T transaction);

    Mono<T> cancel(T transaction, String reference);

    Mono<T> findByCancellationReference(T transaction);

}
