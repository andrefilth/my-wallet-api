package com.amedigital.wallet.model.order.primary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.PrimaryOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PurchaseOrder extends PrimaryOrder {

    private String createdByWalletUuid;

    private PurchaseOrder(Builder builder) {
        super(builder);

        this.createdByWalletUuid = builder.createdByWalletUuid;
    }

    public String getCreatedByWalletUuid() {
        return createdByWalletUuid;
    }

    @Override
    public Builder copy() {
        return new Builder()
                .setId(id)
                .setUuid(uuid)
                .setNsu(nsu)
                .setStatus(status)
                .setAction(action)
                .setTotalAmountInCents(totalAmountInCents)
                .setTitle(title)
                .setDescription(description)
                .setOrderDetailUuid(orderDetailUuid)
                .setAuthorizationMethod(authorizationMethod)
                .setTransactions(transactions)
                .setCreatedByWalletId(createdByWalletId)
                .setUpdatedAt(updatedAt)
                .setCreatedAt(createdAt)
                .setCustomPayload(customPayload)
                .setCreatedByWalletId(createdByWalletId)
                .setReferenceOrderUuid(referenceOrderUuid)
                .setCreatedByWallet(createdByWallet)
                .setCreatedByOwner(createdByOwner)
                .setPaymentMethods(paymentMethods)
                ;
    }

    public static class Builder extends PrimaryOrder.Builder<PurchaseOrder.Builder, PurchaseOrder> {

        private String createdByWalletUuid;

        public Builder() {
            super(OrderType.PURCHASE);
        }

        public Builder setCreatedByWalletUuid(String createdByWalletUuid) {
            this.createdByWalletUuid = createdByWalletUuid;
            return this;
        }

        @Override
        public PurchaseOrder build() {
            return new PurchaseOrder(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("uuid", uuid)
                .append("type", type)
                .append("status", status)
                .append("action", action)
                .append("totalAmountInCents", totalAmountInCents)
                .append("title", title)
                .append("description", description)
                .append("orderDetailUuid", orderDetailUuid)
                .append("authorizationMethod", authorizationMethod)
                .append("createdByWalletId", createdByWalletId)
                .append("transactions", transactions)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("referenceOrderUuid", referenceOrderUuid)
                .append("secondaryId", secondaryId)
                .append("paymentMethods", paymentMethods)
                .append("nsu", nsu)
                .append("createdByWallet", createdByWallet)
                .append("createdByOwner", createdByOwner)
                .append("createdByWalletUuid", createdByWalletUuid)
                .build();
    }
}