package com.amedigital.wallet.service;

import com.amedigital.wallet.model.CreditCard;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditCardTokenService {

    Mono<CreditCard> create(CreditCard card);

    Mono<CreditCard> save(CreditCard card);

    Flux<CreditCard> findByWalletId(Long walletId);

    Mono<CreditCard> deleteCreditCardById(String cardId, Long walletId);

    Mono<CreditCard> findCreditCardByUuid(String cardId, Long walletId);
}
