package com.amedigital.wallet.service.atom.request;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CreditCardParameter {

    private final String securityCode;
    private final String vaultId;
    private final String ownerId;

    private CreditCardParameter(Builder builder) {
        this.securityCode = builder.securityCode;
        this.vaultId = builder.vaultId;
        this.ownerId = builder.ownerId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getVaultId() {
        return vaultId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public static final class Builder {
        private String securityCode;
        private String vaultId;
        private String ownerId;

        public Builder setSecurityCode(String securityCode) {
            this.securityCode = securityCode;
            return this;
        }

        public Builder setVaultId(String vaultId) {
            this.vaultId = vaultId;
            return this;
        }

        public Builder setOwnerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public CreditCardParameter build() {
            return new CreditCardParameter(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("vaultId", vaultId)
                .append("ownerId", ownerId)
                .build();
    }
}
