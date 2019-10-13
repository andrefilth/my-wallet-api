package com.amedigital.wallet.endoint.request.order;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.amedigital.wallet.constants.enuns.OrderType.TRANSFER_BETWEEN_WALLETS;

public class TransferBetweenWalletsOrderRequest extends OrderRequest {

    private String toOwnerId;

    public TransferBetweenWalletsOrderRequest() {
        super(TRANSFER_BETWEEN_WALLETS);
    }

    public String getToOwnerId() {
        return toOwnerId;
    }

    public void setToOwnerId(String toOwnerId) {
        this.toOwnerId = toOwnerId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("toOwnerId", toOwnerId)
                .append("createdByWalletId", createdByWalletId)
                .append("type", type)
                .append("title", title)
                .append("description", description)
                .append("totalAmountInCents", totalAmountInCents)
                .append("paymentMethods", paymentMethods)
                .append("customPayload", customPayload)
                .append("creditWalletId", creditWalletId)
                .append("debitWalletId", debitWalletId)
                .toString();
    }
}
