package com.amedigital.wallet.endoint.converter;

import com.amedigital.wallet.model.Wallet;

import java.util.Optional;

public class RequestContext {

    private final Wallet tokenWallet;
    private final String ownerUuid;
    private final String primaryOrderUuid;
    private final String secondaryId;

    private RequestContext(Builder builder) {
        this.tokenWallet = builder.tokenWallet;
        this.primaryOrderUuid = builder.primaryOrderUuid;
        this.secondaryId = builder.secondaryId;
        this.ownerUuid = builder.ownerUuid;
    }

    public Optional<Wallet> getTokenWallet() {
        return Optional.ofNullable(tokenWallet);
    }

    public Optional<String> getPrimaryOrderUuid() {
        return Optional.ofNullable(primaryOrderUuid);
    }

    public Optional<String> getSecondaryId() {
        return Optional.ofNullable(secondaryId);
    }

    public Optional<String> getOwnerUuid() {
        return Optional.ofNullable(ownerUuid);
    }


    public static Builder build() {
        return new Builder();
    }

    public static class Builder {

        private Wallet tokenWallet;
        private String ownerUuid;
        private String primaryOrderUuid;
        private String secondaryId;

        public Builder setTokenWallet(Wallet tokenWallet) {
            this.tokenWallet = tokenWallet;
            return this;
        }

        public Builder setPrimaryOrderUuid(String primaryOrderUuid) {
            this.primaryOrderUuid = primaryOrderUuid;
            return this;
        }

        public Builder setSecondaryId(String secondaryId) {
            this.secondaryId = secondaryId;
            return this;
        }

        public Builder setOwnerUuid(String ownerUuid) {
            this.ownerUuid = ownerUuid;
            return this;
        }

        public RequestContext build() {
            return new RequestContext(this);
        }
    }
}