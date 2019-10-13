package com.amedigital.wallet.model.order.secondary;

import com.amedigital.wallet.model.order.SecondaryOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.amedigital.wallet.constants.enuns.OrderType.REFUND;

public class RefundOrder extends SecondaryOrder {

    private RefundOrder(Builder builder) {
        super(builder);
    }

    @Override
    public RefundOrder.Builder copy() {
        return new RefundOrder.Builder(referenceOrderUuid)
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
                .setSecondaryId(secondaryId)
                .setCreatedByOwner(createdByOwner)
                .setPaymentMethods(paymentMethods);
    }

    public static class Builder extends SecondaryOrder.Builder<Builder, RefundOrder> {

        public Builder(String referenceOrderUuid) {
            super(referenceOrderUuid, REFUND);
        }

        @Override
        public RefundOrder build() {
            return new RefundOrder(this);
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
                .build();
    }
}