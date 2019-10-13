package com.amedigital.wallet.endoint.request.method;

import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.amedigital.wallet.constants.enuns.PaymentMethod.CREDIT_CARD;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class CreditCardMethodRequest extends MethodRequest {

    private String creditCardId;
    private String cvv;
    private Integer numberOfInstallments;

    public CreditCardMethodRequest() {
        super(CREDIT_CARD);
    }

    public String getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(String creditCardId) {
        this.creditCardId = creditCardId;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("type", type)
                .append("amountInCents", amountInCents)
                .append("creditCardId", creditCardId)
                .append("numberOfInstallments", numberOfInstallments)
                .build();
    }
}
