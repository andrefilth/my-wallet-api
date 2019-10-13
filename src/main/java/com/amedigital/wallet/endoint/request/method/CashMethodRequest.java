package com.amedigital.wallet.endoint.request.method;

import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.amedigital.wallet.constants.enuns.PaymentMethod.CASH;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class CashMethodRequest extends MethodRequest {

    public CashMethodRequest() {
        super(CASH);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("type", type)
                .append("amountInCents", amountInCents)
                .build();
    }
}
