package com.amedigital.wallet.service.impl;

import com.amedigital.wallet.model.CreditCard;
import com.amedigital.wallet.repository.CreditCardTokenRepository;
import com.amedigital.wallet.service.CreditCardTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.util.ValidatorUtil.notEmpty;
import static com.amedigital.wallet.util.ValidatorUtil.notNull;

@Service
public class CreditCardTokenServiceImpl implements CreditCardTokenService {


    private final CreditCardTokenRepository repository;

    @Autowired
    public CreditCardTokenServiceImpl(CreditCardTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<CreditCard> create(CreditCard card) {
        validateCard(card);

        return repository.create(card);
    }

    @Override
    public Mono<CreditCard> save(CreditCard card) {
        validateCard(card);
        return repository.save(card);
    }

    @Override
    public Flux<CreditCard> findByWalletId(Long walletId) {
        notNull(walletId, "Wallet identification");
        return repository.findByWalletId(walletId);
    }

    @Override
    public Mono<CreditCard> deleteCreditCardById(String cardUUID, Long walletId) {
        notEmpty(cardUUID, "Card UUID");
        notNull(walletId, "Wallet Id");

        return repository.findByCardUUID(cardUUID)
                .flatMap(card -> repository.save(card.inactivate(walletId)));

    }

    @Override
    public Mono<CreditCard> findCreditCardByUuid(String cardUUID, Long walletId) {
        notEmpty(cardUUID, "Card UUID");
        notNull(walletId, "Wallet Id");

        return repository.findByCardUUID(cardUUID);
    }

    private void validateCard(CreditCard card) {
        notEmpty(card.getUuid(), "Credit Card");
        notNull(card, "Credit card");
        notNull(card.getBrand(), "Credit card brand");
        notEmpty(card.getHolder(), "Credit card holder");
        notEmpty(card.getMaskedNumber(), "Credit card number");
        notEmpty(card.getExpDate(), "Credit card expiration date");
        notNull(card.getMain(), "Credit card main");
    }
}
