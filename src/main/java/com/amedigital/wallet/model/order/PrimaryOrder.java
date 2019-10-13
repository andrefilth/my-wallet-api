package com.amedigital.wallet.model.order;

import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.transaction.Transaction;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.amedigital.wallet.constants.enuns.OrderStatus.CREATED;
import static java.util.stream.Collectors.toList;

public abstract class PrimaryOrder extends Order {

    protected PrimaryOrder(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.type = builder.type;
        this.status = builder.status;
        this.action = builder.action;
        this.totalAmountInCents = builder.totalAmountInCents;
        this.title = builder.title;
        this.description = builder.description;
        this.orderDetailUuid = builder.orderDetailUuid;
        this.authorizationMethod = builder.authorizationMethod;
        this.createdByWalletId = builder.createdByWalletId;
        this.transactions = builder.transactions;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.customPayload = builder.customPayload;
        this.referenceOrderUuid = builder.referenceOrderUuid;
        this.paymentMethods = builder.paymentMethods;
        this.nsu = builder.nsu;
        this.createdByWallet = builder.createdByWallet;
        this.createdByOwner = builder.createdByOwner;
    }

    @Override
    public OrderLevel getOrderLevel() {
        return OrderLevel.PRIMARY;
    }

    @Override
    public abstract PrimaryOrder.Builder copy();

    public abstract static class Builder<B extends PrimaryOrder.Builder, T extends PrimaryOrder> extends Order.Builder<B, T> {

        protected final OrderType type;

        protected Long id;
        protected String uuid;
        protected OrderStatus status = CREATED;
        protected Action action;
        protected Long totalAmountInCents;
        protected String title;
        protected String description;
        protected String orderDetailUuid;
        protected AuthorizationMethod authorizationMethod;
        protected List<Transaction> transactions;
        protected Long createdByWalletId;
        protected ZonedDateTime createdAt;
        protected ZonedDateTime updatedAt;
        protected Map<String, Object> customPayload;
        protected String referenceOrderUuid;
        protected Wallet createdByWallet;
        protected Owner createdByOwner;

        public Builder(OrderType type) {
            this.type = type;
        }

        public B setId(Long id) {
            this.id = id;
            return (B) this;
        }

        public B setUuid(String uuid) {
            this.uuid = uuid;
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
            setPaymentMethods(Optional.ofNullable(transactions).orElse(Collections.emptyList())
                    .stream().map(Transaction::getPaymentMethod).map(PaymentMethod::name).collect(toList()));
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