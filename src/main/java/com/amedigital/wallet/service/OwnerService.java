package com.amedigital.wallet.service;

import com.amedigital.wallet.model.Owner;
import reactor.core.publisher.Mono;

public interface OwnerService {

    Mono<Owner> findByDocument(String document);

    Mono<Owner> findByEmail(String email);

    Mono<Owner> findByWalletId(Long walletId);

}
