package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.model.order.Order;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TransferBetweenWalletsResponse extends OrderResponse {

    private final OwnerResponse creditOwner;
    private final OwnerResponse debitOwner;

    public TransferBetweenWalletsResponse(Order order, Owner creditOwner) {
        super(order);

        this.debitOwner = this.getCreatedByOwner();
        this.creditOwner = new OwnerResponse(creditOwner);
    }

    public OwnerResponse getCreditOwner() {
        return creditOwner;
    }

    public OwnerResponse getDebitOwner() {
        return debitOwner;
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
                .append("creditOwner", creditOwner)
                .append("debitOwner", debitOwner)
                .build();
    }
}
