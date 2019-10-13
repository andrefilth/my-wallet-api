package com.amedigital.wallet.repository;

import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.WalletBalance;
import reactor.core.publisher.Mono;

public interface WalletRepository {

    Mono<Wallet> insert(Wallet wallet);

    Mono<Wallet> findByUuid(String uuid);

    Mono<Wallet> findById(Long id);

    Mono<Wallet> findByOwnerUuid(String ownerUuid);

    Mono<Wallet> update(Wallet wallet);

    Mono<WalletBalance> findBalanceByWalletId(Long id);
}
