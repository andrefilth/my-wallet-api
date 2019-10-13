package com.amedigital.wallet.model;

import java.time.ZonedDateTime;

public class TransactionDataItem {

    private final String transactionId;
    private final String orderId;
    private final Long amountInCents;
    private final ZonedDateTime createdAt;

    public Long getAmountInCents() {
        return amountInCents;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    private TransactionDataItem(final String transactionId, final String orderId, final Long amountInCents, final ZonedDateTime createdAt) {
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.amountInCents = amountInCents;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public final static class Builder {
        private String transactionId;
        private String orderId;
        private Long amountInCents;
        private ZonedDateTime createdAt;

        private Builder() {
        }

        public Builder setTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setAmountInCents(Long amountInCents) {
            this.amountInCents = amountInCents;
            return this;
        }

        public Builder setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TransactionDataItem build() {
            return new TransactionDataItem(transactionId, orderId, amountInCents, createdAt);
        }


    }
}
