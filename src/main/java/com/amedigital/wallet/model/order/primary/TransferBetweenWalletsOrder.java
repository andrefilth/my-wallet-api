package com.amedigital.wallet.model.order.primary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.PrimaryOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TransferBetweenWalletsOrder extends PrimaryOrder {

    private final Long toWalletId;

    private String toWalletUuid;

    private String toOwnerUuid;

    private TransferBetweenWalletsOrder(Builder builder) {
        super(builder);

        this.toWalletId = builder.toWalletId;
        this.toWalletUuid = builder.toWalletUuid;
        this.toOwnerUuid = builder.toOwnerUuid;
    }

    public Long getToWalletId() {
        return toWalletId;
    }

    public String getToWalletUuid() {
        return toWalletUuid;
    }

    public String getToOwnerUuid() {
        return toOwnerUuid;
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
                .setToWalletId(toWalletId)
                .setToWalletUuid(toWalletUuid)
                .setReferenceOrderUuid(referenceOrderUuid)
                .setCreatedByOwner(createdByOwner)
                .setPaymentMethods(paymentMethods);

    }

    public static class Builder extends PrimaryOrder.Builder<TransferBetweenWalletsOrder.Builder, TransferBetweenWalletsOrder> {
        private Long toWalletId;
        private String toWalletUuid;
        private String toOwnerUuid;

        public Builder() {
            super(OrderType.TRANSFER_BETWEEN_WALLETS);
        }

        public Builder setToWalletId(Long toWalletId) {
            this.toWalletId = toWalletId;
            return this;
        }

        public Builder setToWalletUuid(String toWalletUuid) {
            this.toWalletUuid = toWalletUuid;
            return this;
        }

        public Builder setToOwnerUuid(String toOwnerUuid) {
            this.toOwnerUuid = toOwnerUuid;
            return this;

        }

        @Override
        public TransferBetweenWalletsOrder build() {
            return new TransferBetweenWalletsOrder(this);
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
                .append("createdByOwner", createdByOwner)
                .append("toWalletId", toWalletId)
                .append("toWalletUuid", toWalletUuid)
                .append("toOwnerUuid", toOwnerUuid)
                .build();
    }
}
