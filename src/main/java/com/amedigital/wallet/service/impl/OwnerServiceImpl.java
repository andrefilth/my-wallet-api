package com.amedigital.wallet.service.impl;

import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.repository.OwnerRepository;
import com.amedigital.wallet.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    @Autowired
    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Mono<Owner> findByDocument(String document) {
        return this.ownerRepository.findByDocument(document);
    }

    @Override
    public Mono<Owner> findByEmail(String email) {
        return this.ownerRepository.findByEmail(email);
    }

    @Override
    public Mono<Owner> findByWalletId(Long walletId) {
        return this.ownerRepository.findByWalletId(walletId);
    }
}