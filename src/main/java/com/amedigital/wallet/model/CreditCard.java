package com.amedigital.wallet.model;

import com.amedigital.wallet.constants.enuns.CreditCardState;
import com.amedigital.wallet.service.atom.response.enums.CardBrand;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CreditCard {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

    private final Long id;

    private final String uuid;

    private final String token;

    private final String hash;

    private final String holder;

    private final String maskedNumber;

    private final CardBrand brand;

    private final String expDate;

    private final boolean main;

    private final Long walletId;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private final boolean active;

    private final boolean verifiedByAme;

    private CreditCard(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.hash = builder.hash;
        this.holder = builder.holder;
        this.maskedNumber = builder.maskedNumber;
        this.brand = builder.brand;
        this.expDate = builder.expDate;
        this.main = builder.main;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.token = builder.token;
        this.walletId = builder.walletId;
        this.active = builder.active;
        this.verifiedByAme = builder.verifiedByAme;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
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

    public String getToken() {
        return token;
    }

    public Long getWalletId() {
        return walletId;
    }

    public boolean getActive() {
        return active;
    }

    public boolean getVerifiedByAme() {
        return verifiedByAme;
    }

    public CreditCardState getStatus() {
        return (expDate != null && isExpired(this.getExpDate())) ? CreditCardState.EXPIRED : CreditCardState.AVAILABLE;
    }

    private boolean isExpired(String expDate) {
        return isBeforeToday(YearMonth.parse(expDate, formatter));
    }

    private boolean isBeforeToday(YearMonth date) {
        return date.isBefore(YearMonth.now());
    }

    private Builder baseCopy(Long walletId) {
        return builder().setExpDate(expDate)
                .setBrand(brand)
                .setUpdatedAt(updatedAt)
                .setCreatedAt(createdAt)
                .setHash(hash)
                .setId(id)
                .setUuid(uuid)
                .setToken(token)
                .setMaskedNumber(maskedNumber)
                .setHolder(holder)
                .setWalletId(walletId)
                .setVerifiedByAme(verifiedByAme);
    }

    public CreditCard copy() {
        return baseCopy(walletId)
                .setMain(main)
                .setActive(active)
                .build();
    }

    public CreditCard inactivate(Long walletId) {
        return baseCopy(walletId)
                .setMain(false)
                .setActive(false)
                .build();
    }

    public static class Builder {
        Long id;

        String uuid;

        String hash;

        String holder;

        String maskedNumber;

        CardBrand brand;

        String expDate;

        boolean main;

        LocalDateTime createdAt;

        LocalDateTime updatedAt;

        String token;

        Long walletId;

        boolean active;

        boolean verifiedByAme;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setHash(String hash) {
            this.hash = hash;
            return this;
        }

        public Builder setHolder(String holder) {
            this.holder = holder;
            return this;
        }

        public Builder setMaskedNumber(String maskedNumber) {
            this.maskedNumber = maskedNumber;
            return this;
        }

        public Builder setBrand(CardBrand brand) {
            this.brand = brand;
            return this;
        }

        public Builder setExpDate(String expDate) {
            this.expDate = expDate;
            return this;
        }

        public Builder setMain(boolean main) {
            this.main = main;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setWalletId(Long walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder setActive(boolean active) {

            this.active = active;
            return this;
        }

        public Builder setVerifiedByAme(boolean verifiedByAme) {
            this.verifiedByAme = verifiedByAme;
            return this;
        }

        public CreditCard build() {
            return new CreditCard(this);
        }

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("uuid", uuid)
                .append("token", token)
                .append("hash", hash)
                .append("holder", holder)
                .append("maskedNumber", maskedNumber)
                .append("brand", brand)
                .append("expDate", expDate)
                .append("main", main)
                .append("walletId", walletId)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("active", active)
                .append("verifiedByAme", verifiedByAme)
                .build();
    }
}
