package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.constants.enuns.WalletType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WalletReferenceResponse {

    private WalletType type;
    private OwnerReferenceResponse owner;

    public WalletReferenceResponse(WalletType type, OwnerReferenceResponse owner) {
        this.type = type;
        this.owner = owner;
    }

    public WalletType getType() {
        return type;
    }

    public OwnerReferenceResponse getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("type", type)
                .append("owner", owner)
                .build();
    }
}
