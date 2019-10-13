package com.amedigital.wallet.repository;

import com.amedigital.wallet.model.Owner;
import org.jdbi.v3.core.Handle;
import reactor.core.publisher.Mono;

public interface OwnerRepository {

    Mono<Owner> findByDocument(String document);

    Mono<Owner> findByEmail(String email);

    Owner insert(Handle handle, Owner owner);

    Owner update(Handle handle, Owner owner);

    Mono<Owner> findByWalletId(Long walletId);

    Owner findByWalletId(Handle handle, Long walletId);
}
