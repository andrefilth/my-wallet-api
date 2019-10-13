package com.amedigital.wallet.service;

import com.amedigital.wallet.model.Wallet;
import reactor.core.publisher.Mono;

public interface WalletService {

    Mono<Wallet> create(Wallet wallet);

    Mono<Wallet> findByUuid(String walletId);

    Mono<Wallet> findById(Long id);

    Mono<Wallet> findByOwnerUuid(String ownerUuid);

    Mono<Wallet> update(Wallet wallet, final String walletId);

}
