package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.constants.enuns.CreditCardState;
import com.amedigital.wallet.model.CreditCard;
import com.amedigital.wallet.service.atom.response.enums.CardBrand;

import java.time.LocalDateTime;

public class CreditCardResponse {

    private String id;

    private String token;

    private String hash;

    private String holder;

    private String maskedNumber;

    private CardBrand brand;

    private CreditCardState status;

    private String expDate;

    private boolean main;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean verifiedByAme;

    public CreditCardResponse(CreditCard card) {
        id = card.getUuid();
        token = card.getToken();
        hash = card.getHash();
        holder = card.getHolder();
        maskedNumber = card.getMaskedNumber();
        brand = card.getBrand();
        expDate = card.getExpDate();
        main = card.getMain();
        createdAt = card.getCreatedAt();
        updatedAt = card.getUpdatedAt();
        status = card.getStatus();
        verifiedByAme = card.getVerifiedByAme();
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getHash() {
        return hash;
    }

    public String getHolder() {
        return holder;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public CardBrand getBrand() {
        return brand;
    }

    public String getExpDate() {
        return expDate;
    }

    public boolean getMain() {
        return main;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean getVerifiedByAme() {
        return verifiedByAme;
    }

    public CreditCardState getStatus() {
        return status;
    }
}
