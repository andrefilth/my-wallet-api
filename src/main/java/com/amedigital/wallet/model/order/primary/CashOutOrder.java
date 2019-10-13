package com.amedigital.wallet.model.order.primary;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.PrimaryOrder;

public class CashOutOrder extends PrimaryOrder {

	private CashOutOrder(Builder builder) {
		super(builder);
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
                .setReferenceOrderUuid(referenceOrderUuid)
                .setCreatedByOwner(createdByOwner)
                .setPaymentMethods(paymentMethods);
	}
	
    public static class Builder extends PrimaryOrder.Builder<CashOutOrder.Builder, CashOutOrder> {

        public Builder() {
            super(OrderType.CASH_OUT);
        }

        @Override
        public CashOutOrder build() {
            return new CashOutOrder(this);
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
