package com.amedigital.wallet.repository;

import com.amedigital.wallet.endoint.response.SimpleCreditCardResponse;
import com.amedigital.wallet.model.CreditCard;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditCardTokenRepository {

    Mono<CreditCard> save(CreditCard card);

    Mono<CreditCard> create(CreditCard card);

    Mono<CreditCard> findByCardUUID(String cardUUID);

    Flux<CreditCard> findByWalletId(Long walletId);

    Mono<SimpleCreditCardResponse> findByOrderUuid(final String orderUuid);
}
