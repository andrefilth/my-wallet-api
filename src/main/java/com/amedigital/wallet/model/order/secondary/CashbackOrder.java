package com.amedigital.wallet.model.order.secondary;

import com.amedigital.wallet.model.order.SecondaryOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.amedigital.wallet.constants.enuns.OrderType.CASH_BACK;

public class CashbackOrder extends SecondaryOrder {

    private CashbackOrder(Builder builder) {
        super(builder);
    }

    @Override
    public Builder copy() {
        return new Builder(referenceOrderUuid)
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

    public static class Builder extends SecondaryOrder.Builder<Builder, CashbackOrder> {

        public Builder(String referenceOrderUuid) {
            super(referenceOrderUuid, CASH_BACK);
        }

        @Override
        public CashbackOrder build() {
            return new CashbackOrder(this);
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
