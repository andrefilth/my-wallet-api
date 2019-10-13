package com.amedigital.wallet.service.atom.request;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class PaymentParameter {

    private final String orderReference;
    private final List<PaymentItem> payments;

    private PaymentParameter(Builder builder) {
        this.orderReference = builder.orderReference;
        this.payments = builder.payments;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public List<PaymentItem> getPayments() {
        return payments;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String orderReference;
        private List<PaymentItem> payments;

        public Builder setOrderReference(String orderReference) {
            this.orderReference = orderReference;
            return this;
        }

        public Builder setPayments(List<PaymentItem> payments) {
            this.payments = payments;
            return this;
        }

        public PaymentParameter build() {
            return new PaymentParameter(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("orderReference", orderReference)
                .append("payments", payments)
                .build();
    }
}
