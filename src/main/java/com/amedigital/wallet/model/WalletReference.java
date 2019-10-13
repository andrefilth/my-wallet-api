package com.amedigital.wallet.model;

import com.amedigital.wallet.constants.enuns.WalletType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WalletReference {

    private final WalletType type;
    private final OwnerReference owner;

    private WalletReference(Builder builder) {
        this.type = builder.type;
        this.owner = builder.owner;
    }

    public WalletType getType() {
        return type;
    }

    public OwnerReference getOwner() {
        return owner;
    }

    public static final class Builder {
        private WalletType type;
        private OwnerReference owner;

        public Builder setType(WalletType type) {
            this.type = type;
            return this;
        }

        public Builder setOwner(OwnerReference owner) {
            this.owner = owner;
            return this;
        }

        public WalletReference build() {
            return new WalletReference(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("type", type)
                .append("owner", owner)
                .build();
    }
}
