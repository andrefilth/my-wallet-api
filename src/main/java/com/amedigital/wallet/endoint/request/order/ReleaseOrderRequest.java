package com.amedigital.wallet.endoint.request.order;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Objects;

import static com.amedigital.wallet.constants.enuns.OrderType.RELEASE;

public class ReleaseOrderRequest extends OrderRequest {

    private List<PaymentMethodRuleRequest> rules;

    public ReleaseOrderRequest(){
        super(RELEASE);
    }

    public List<PaymentMethodRuleRequest> getPaymentMethodRules() {
        return rules;
    }

    public void setRules(List<PaymentMethodRuleRequest> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("creditWalletId", creditWalletId)
                .append("debitWalletId", debitWalletId)
                .append("createdByWalletId", createdByWalletId)
                .append("type", type)
                .append("title", title)
                .append("description", description)
                .append("totalAmountInCents", totalAmountInCents)
                .append("paymentMethods", paymentMethods)
                .append("rules", rules)
                .build();
    }
}
