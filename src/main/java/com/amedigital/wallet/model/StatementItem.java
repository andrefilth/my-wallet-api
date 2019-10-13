package com.amedigital.wallet.model;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TransactionType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;
import java.util.List;

public class StatementItem {

    private final String id;
    private final OrderType type;
    private final TransactionType transactionType;
    private final String title;
    private final String description;
    private final OrderStatus status;
    private final Long amountInCents;
    private final List<PaymentMethod> paymentMethods;
    private final String orderReference;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final WalletReference walletReference;
    private final Long netAmountInCents;

    private StatementItem(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.transactionType = builder.transactionType;
        this.title = builder.title;
        this.description = builder.description;
        this.status = builder.status;
        this.amountInCents = builder.amountInCents;
        this.paymentMethods = builder.paymentMethods;
        this.orderReference = builder.orderReference;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.walletReference = builder.walletReference;
        this.netAmountInCents = builder.netAmountInCents;
    }

    public String getId() {
        return id;
    }

    public OrderType getType() {
        return type;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public WalletReference getWalletReference() {
        return walletReference;
    }
    
    public Long getNetAmountInCents() {
    	return netAmountInCents;
    }

    public Builder copy() {
        return new Builder()
                .setId(id)
                .setType(type)
                .setTransactionType(transactionType)
                .setTitle(title)
                .setDescription(description)
                .setStatus(status)
                .setAmountInCents(amountInCents)
                .setPaymentMethods(paymentMethods)
                .setOrderReference(orderReference)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt)
                .setWalletReference(walletReference);
    }

    public static final class Builder {
        private String id;
        private OrderType type;
        private TransactionType transactionType;
        private String title;
        private String description;
        private OrderStatus status;
        private Long amountInCents;
        private List<PaymentMethod> paymentMethods;
        private String orderReference;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private WalletReference walletReference;
        private Long netAmountInCents;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setType(OrderType type) {
            this.type = type;
            return this;
        }

        public Builder setTransactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder setAmountInCents(Long amountInCents) {
            this.amountInCents = amountInCents;
            return this;
        }

        public Builder setPaymentMethods(List<PaymentMethod> paymentMethods) {
            this.paymentMethods = paymentMethods;
            return this;
        }

        public Builder setOrderReference(String orderReference) {
            this.orderReference = orderReference;
            return this;
        }

        public Builder setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setWalletReference(WalletReference walletReference) {
            this.walletReference = walletReference;
            return this;
        }
        
        public Builder setNetAmountInCents(Long netAmountInCents) {
        	this.netAmountInCents = netAmountInCents;
        	return this;
        }

        public StatementItem build() {
            return new StatementItem(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("type", type)
                .append("transactionType", transactionType)
                .append("title", title)
                .append("description", description)
                .append("status", status)
                .append("amountInCents", amountInCents)
                .append("paymentMethods", paymentMethods)
                .append("orderReference", orderReference)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("walletReference", walletReference)
                .append("netAmountInCents", netAmountInCents)
                .build();
    }
}
