package com.amedigital.wallet.endoint.response.transaction;

import com.amedigital.wallet.model.transaction.CashBackTransaction;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class CashBackTransactionResponse extends TransactionResponse {

    public CashBackTransactionResponse(CashBackTransaction cashBackTransaction) {
        super(cashBackTransaction);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("id", id)
                .append("type", type)
                .append("paymentMethod", paymentMethod)
                .append("amountInCents", amountInCents)
                .append("status", status)
                .append("walletId", walletId)
                .append("peerWalletId", peerWalletId)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .build();
    }
}
