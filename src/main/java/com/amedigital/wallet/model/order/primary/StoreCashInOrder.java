package com.amedigital.wallet.model.order.primary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.PrimaryOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StoreCashInOrder extends PrimaryOrder {

    private String creditWalletUUID;
    private Long creditWalletId;

    private StoreCashInOrder(Builder builder) {
        super(builder);

        creditWalletUUID = builder.creditWalletUUID;
        creditWalletId = builder.creditWalletId;
    }

    public String getCreditWalletUUID() {
        return creditWalletUUID;
    }

    public Long getCreditWalletId() {
        return creditWalletId;
    }

    @Override
    public Builder copy() {
        return new StoreCashInOrder.Builder()
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
                .setReferenceOrderUuid(referenceOrderUuid)
                .setCreatedByOwner(createdByOwner)
                .setPaymentMethods(paymentMethods)
                .setCreditWalletUUID(creditWalletUUID);
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
                .append("creditWalletUUID", creditWalletUUID)
                .build();
    }

    public static class Builder extends PrimaryOrder.Builder<StoreCashInOrder.Builder, StoreCashInOrder> {

        String creditWalletUUID;
        Long creditWalletId;

        public Builder() {
            super(OrderType.STORE_CASH_IN);
        }

        public Builder setCreditWalletUUID(String creditWalletUUID) {
            this.creditWalletUUID = creditWalletUUID;
            return this;
        }

        public Builder setCreditWalletId(Long creditWalletId) {
            this.creditWalletId = creditWalletId;
            return this;
        }

        @Override
        public StoreCashInOrder build() {
            return new StoreCashInOrder(this);
        }
    }
}
