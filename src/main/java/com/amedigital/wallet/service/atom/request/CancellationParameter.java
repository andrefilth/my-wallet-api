package com.amedigital.wallet.service.atom.request;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CancellationParameter {

    private final String reference;
    private final Long amountInCents;

    public CancellationParameter(String reference, Long amountInCents) {
        this.reference = reference;
        this.amountInCents = amountInCents;
    }

    public String getReference() {
        return reference;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("reference", reference)
                .append("amountInCents", amountInCents)
                .build();
    }
}
