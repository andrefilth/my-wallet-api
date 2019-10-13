package com.amedigital.wallet.model.order;

import com.amedigital.wallet.commons.Build;
import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.transaction.Transaction;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Order {

    protected Long id;
    protected String uuid;
    protected OrderType type;
    protected OrderStatus status;
    protected Action action;
    protected Long totalAmountInCents;
    protected String title;
    protected String description;
    protected String orderDetailUuid;
    protected AuthorizationMethod authorizationMethod;
    protected Long createdByWalletId;
    protected List<Transaction> transactions;
    protected ZonedDateTime createdAt;
    protected ZonedDateTime updatedAt;
    protected Map<String, Object> customPayload;
    protected String referenceOrderUuid;
    protected String secondaryId;
    protected List<String> paymentMethods;
    protected String nsu;
    protected Owner createdByOwner;
    protected Wallet createdByWallet;

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public OrderType getType() {
        return type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Action getAction() {
        return action;
    }

    public Long getTotalAmountInCents() {
        return totalAmountInCents;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOrderDetailUuid() {
        return orderDetailUuid;
    }

    public AuthorizationMethod getAuthorizationMethod() {
        return authorizationMethod;
    }

    public Long getCreatedByWalletId() {
        return createdByWalletId;
    }

    public List<Transaction> getTransactions() {
        return transactions != null ? transactions : Collections.emptyList();
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Map<String, Object> getCustomPayload() {
        return customPayload != null ? customPayload : Collections.emptyMap();
    }

    public String getReferenceOrderUuid() {
        return referenceOrderUuid;
    }

    public abstract OrderLevel getOrderLevel();

    public String getSecondaryId() {
        return secondaryId;
    }

    public List<String> getPaymentMethods() {
        return paymentMethods != null ? paymentMethods : Collections.emptyList();
    }

    public String getNsu() {
        return nsu;
    }

    public Wallet getCreatedByWallet() {
        return createdByWallet;
    }

    public Owner getCreatedByOwner() {
        return createdByOwner;
    }

    public abstract Builder copy();



    public static abstract class Builder<B extends Builder, T extends Order> implements Build<T> {

        protected Long id;
        protected String uuid;
        protected OrderType type;
        protected OrderStatus status;
        protected Action action;
        protected Long totalAmountInCents;
        protected String title;
        protected String description;
        protected String orderDetailUuid;
        protected AuthorizationMethod authorizationMethod;
        protected Long createdByWalletId;
        protected List<Transaction> transactions;
        protected ZonedDateTime createdAt;
        protected ZonedDateTime updatedAt;
        protected Map<String, Object> customPayload;
        protected String referenceOrderUuid;
        protected String secondaryId;
        protected List<String> paymentMethods;
        protected String nsu;
        protected Wallet createdByWallet;
        protected Owner createdByOwner;

        public B setId(Long id) {
            this.id = id;
            return (B) this;
        }

        public B setUuid(String uuid) {
            this.uuid = uuid;
            return (B) this;
        }

        public B setType(OrderType type) {
            this.type = type;
            return (B) this;
        }

        public B setStatus(OrderStatus status) {
            this.status = status;
            return (B) this;
        }

        public B setAction(Action action) {
            this.action = action;
            return (B) this;
        }

        public B setTotalAmountInCents(Long totalAmountInCents) {
            this.totalAmountInCents = totalAmountInCents;
            return (B) this;
        }

        public B setTitle(String title) {
            this.title = title;
            return (B) this;
        }

        public B setDescription(String description) {
            this.description = description;
            return (B) this;
        }

        public B setOrderDetailUuid(String orderDetailUuid) {
            this.orderDetailUuid = orderDetailUuid;
            return (B) this;
        }

        public B setAuthorizationMethod(AuthorizationMethod authorizationMethod) {
            this.authorizationMethod = authorizationMethod;
            return (B) this;
        }

        public B setCreatedByWalletId(Long createdByWalletId) {
            this.createdByWalletId = createdByWalletId;
            return (B) this;
        }

        public B setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
            setPaymentMethods(Optional.ofNullable(transactions).orElse(Collections.emptyList()).stream().map(Transaction::getPaymentMethod)
                    .map(PaymentMethod::name).collect(Collectors.toList()));
            return (B) this;
        }

        public B setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return (B) this;
        }


        public B setUpdatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return (B) this;
        }

        public B setCustomPayload(Map<String, Object> customPayload) {
            this.customPayload = customPayload;
            return (B) this;
        }

        public B setReferenceOrderUuid(String referenceOrderUuid) {
            this.referenceOrderUuid = referenceOrderUuid;
            return (B) this;
        }

        public B setSecondaryId(String secondaryId) {
            this.secondaryId = secondaryId;
            return (B) this;
        }

        public B setPaymentMethods(List<String> paymentMethods) {
            this.paymentMethods = paymentMethods;
            return (B) this;
        }

        public B setNsu(String nsu) {
            this.nsu = nsu;
            return (B) this;
        }

        public B setCreatedByWallet(Wallet createdByWallet) {
            this.createdByWallet = createdByWallet;
            return (B) this;
        }

        public B setCreatedByOwner(Owner createdByOwner) {
            this.createdByOwner = createdByOwner;
            return (B) this;
        }
    }

}
