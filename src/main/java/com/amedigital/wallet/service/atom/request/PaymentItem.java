package com.amedigital.wallet.service.atom.request;

import com.amedigital.wallet.service.atom.response.enums.Currency;
import com.amedigital.wallet.service.atom.response.enums.InstallmentType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PaymentItem {

    private final String paymentReference;
    private final Long amountInCents;
    private final Currency currency;
    private final Integer installments;
    private final InstallmentType installmentType;
    private final String businessUnit;
    private final CreditCardParameter creditCard;
    private final String softDescriptor;

    private PaymentItem(Builder builder) {
        this.paymentReference = builder.paymentReference;
        this.amountInCents = builder.amountInCents;
        this.currency = builder.currency;
        this.installments = builder.installments;
        this.installmentType = builder.installmentType;
        this.businessUnit = builder.businessUnit;
        this.creditCard = builder.creditCard;
        this.softDescriptor = builder.softDescriptor;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Integer getInstallments() {
        return installments;
    }

    public InstallmentType getInstallmentType() {
        return installmentType;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public CreditCardParameter getCreditCard() {
        return creditCard;
    }

    public String getSoftDescriptor() {
        return softDescriptor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String paymentReference;
        private Long amountInCents;
        private Currency currency;
        private Integer installments;
        private InstallmentType installmentType;
        private String businessUnit;
        private CreditCardParameter creditCard;
        private String softDescriptor;

        public Builder setPaymentReference(String paymentReference) {
            this.paymentReference = paymentReference;
            return this;
        }

        public Builder setAmountInCents(Long amountInCents) {
            this.amountInCents = amountInCents;
            return this;
        }

        public Builder setCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder setInstallments(Integer installments) {
            this.installments = installments;
            return this;
        }

        public Builder setInstallmentType(InstallmentType installmentType) {
            this.installmentType = installmentType;
            return this;
        }

        public Builder setBusinessUnit(String businessUnit) {
            this.businessUnit = businessUnit;
            return this;
        }

        public Builder setCreditCard(CreditCardParameter creditCard) {
            this.creditCard = creditCard;
            return this;
        }

        public Builder setSoftDescriptor(String softDescriptor) {
            this.softDescriptor = softDescriptor;
            return this;
        }

        public PaymentItem build() {
            return new PaymentItem(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("paymentReference", paymentReference)
                .append("amountInCents", amountInCents)
                .append("currency", currency)
                .append("installments", installments)
                .append("installmentType", installmentType)
                .append("businessUnit", businessUnit)
                .append("creditCard", creditCard)
                .append("softDescriptor", softDescriptor)
                .build();
    }
}