package com.amedigital.wallet.model;

import com.amedigital.wallet.constants.enuns.WalletType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;
import java.util.Optional;

public class Wallet {

    private final Long id;
    private final String uuid;
    private final String name;
    private final WalletType type;
    private final boolean main;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final Owner owner;
    private final WalletBalance balance;

    private Wallet(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.type = builder.type;
        this.main = builder.main;
        this.owner = builder.owner;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.balance = builder.balance;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getUuid() {
        return Optional.ofNullable(uuid);
    }

    public String getName() {
        return name;
    }

    public WalletType getType() {
        return type;
    }

    public boolean isMain() {
        return main;
    }

    public Owner getOwner() {
        return owner;
    }

    public boolean isMerchant()  {
        return WalletType.MERCHANT.equals(this.type);
    }

    public boolean isCustomer() {
        return WalletType.CUSTOMER.equals(this.type);
    }

    public WalletBalance getBalance() {
        return balance;
    }

    public Builder copy() {
        return new Builder()
                .setCreatedAt(createdAt)
                .setId(id)
                .setName(name)
                .setUpdatedAt(updatedAt)
                .setUuid(uuid)
                .setMain(main)
                .setOwner(owner)
                .setType(type)
                .setBalance(balance);

    }

    public Optional<ZonedDateTime> getCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public Optional<ZonedDateTime> getUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("uuid", uuid)
                .append("name", name)
                .append("type", type)
                .append("main", main)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("owner", owner)
                .append("balance", balance)
                .build();
    }

    public static final class Builder {
        private Long id;
        private String uuid;
        private String name;
        private WalletType type;
        private boolean main;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private Owner owner;
        private WalletBalance balance = new WalletBalance(Balance.emptyBalance(), Balance.emptyBalance());

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(WalletType type) {
            this.type = type;
            return this;
        }

        public Builder setMain(boolean main) {
            this.main = main;
            return this;
        }

        public Builder setOwner(Owner owner) {
            this.owner = owner;
            return this;
        }

        public Builder setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setBalance(WalletBalance balance) {
            this.balance = balance;
            return this;
        }

        public Wallet build() {
            return new Wallet(this);
        }
    }
}
