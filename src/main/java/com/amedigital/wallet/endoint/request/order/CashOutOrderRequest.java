package com.amedigital.wallet.endoint.request.order;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.amedigital.wallet.constants.enuns.OrderType;

public class CashOutOrderRequest extends OrderRequest {

	public CashOutOrderRequest() {
		super(OrderType.CASH_OUT);
	}

	@Override
	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("createdByWalletId", createdByWalletId)
                .append("type", type)
                .append("title", title)
                .append("description", description)
                .append("totalAmountInCents", totalAmountInCents)
                .append("paymentMethods", paymentMethods)
                .build();
    }
}
