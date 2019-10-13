package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.model.order.Order;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PurchaseOrderWalletResponse extends OrderResponse {

	private String externalId;

	public PurchaseOrderWalletResponse(Order order, String externalId) {
		super(order);
		this.externalId = externalId;
	}

	public String getExternalId() {
		return externalId;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("type", type)
                .append("externalId", externalId)
                .append("status", status)
                .append("title", title)
                .append("description", description)
                .append("totalAmountInCents", totalAmountInCents)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("nsu", nsu)
                .append("transactionType", transactionType)
                .append("paymentMethods", paymentMethods)
                .append("action", action)
                .append("transactions", transactions)
                .append("createdByWallet", createdByWallet)
                .append("createdByOwner", createdByOwner)
                .append("orderDetailUuid", orderDetailUuid)
                .build();
	}

}
