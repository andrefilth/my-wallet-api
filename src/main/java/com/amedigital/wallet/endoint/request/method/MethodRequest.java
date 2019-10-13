package com.amedigital.wallet.endoint.request.method;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreditCardMethodRequest.class, name = "CREDIT_CARD"),
        @JsonSubTypes.Type(value = CashMethodRequest.class, name = "CASH"),
        @JsonSubTypes.Type(value = BankTransferMethodRequest.class, name = "BANK_TRANSFER"),
        @JsonSubTypes.Type(value = CashBackMethodRequest.class, name = "CASH_BACK")
})
public abstract class MethodRequest {

    protected final PaymentMethod type;
    protected Long amountInCents;

    public MethodRequest(PaymentMethod type) {
        this.type = type;
    }

    public PaymentMethod getType() {
        return type;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }

}
