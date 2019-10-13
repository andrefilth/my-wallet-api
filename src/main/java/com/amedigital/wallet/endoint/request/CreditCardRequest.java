package com.amedigital.wallet.endoint.request;

import com.amedigital.wallet.model.CreditCard;
import com.amedigital.wallet.service.atom.response.enums.CardBrand;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CreditCardRequest {
    private String id;
    private String token;
    private String hash;
    private String holder;
    private String maskedNumber;
    private CardBrand brand;
    private String expDate;
    private Boolean main;
    private Boolean verifiedByAme;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public CardBrand getBrand() {
        return brand;
    }

    public void setBrand(CardBrand brand) {
        this.brand = brand;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public boolean getMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public boolean getVerifiedByAme() {
        return verifiedByAme;
    }

    public void setVerifiedByAme(boolean verifiedByAme) {
        this.verifiedByAme = verifiedByAme;
    }

    public CreditCard toModel(Long walletId, String uuid) {
        return CreditCard.builder()
                .setToken(token)
                .setUuid(id)
                .setHash(hash)
                .setHolder(holder)
                .setMaskedNumber(maskedNumber)
                .setBrand(brand)
                .setExpDate(expDate)
                .setMain(main != null ? main : Boolean.TRUE)
                .setVerifiedByAme(verifiedByAme != null ? verifiedByAme : Boolean.FALSE)
                .setActive(true)
                .setUuid(uuid)
                .setWalletId(walletId)
                .build();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("token", token)
                .append("hash", hash)
                .append("holder", holder)
                .append("maskedNumber", maskedNumber)
                .append("brand", brand)
                .append("expDate", expDate)
                .append("main", main)
                .append("verifiedByAme", verifiedByAme)
                .build();
    }
}
