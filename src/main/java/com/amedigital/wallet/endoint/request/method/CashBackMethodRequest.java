package com.amedigital.wallet.endoint.request.method;

import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.amedigital.wallet.constants.enuns.PaymentMethod.CASH_BACK;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class CashBackMethodRequest extends MethodRequest {

    public CashBackMethodRequest() {
        super(CASH_BACK);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("type", type)
                .append("amountInCents", amountInCents)
                .build();
    }

}
