package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.model.order.SecondaryOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SecondaryOrderResponse extends OrderResponse {

    private final String secondaryId;

    public SecondaryOrderResponse(SecondaryOrder order) {
        super(order);

        secondaryId = order.getSecondaryId();
    }

    public String getSecondaryId() {
        return secondaryId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("type", type)
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
                .append("createdByOwner", createdByOwner)
                .append("orderDetailUuid", orderDetailUuid)
                .append("secondaryId", secondaryId)
                .build();
    }

}