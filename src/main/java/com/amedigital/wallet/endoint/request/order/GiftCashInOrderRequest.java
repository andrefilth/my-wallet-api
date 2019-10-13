package com.amedigital.wallet.endoint.request.order;

import com.amedigital.wallet.constants.enuns.OrderType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class GiftCashInOrderRequest extends OrderRequest {

    private Long amountPerWalletInCents;
    private List<String> customersWalletIds;

    public GiftCashInOrderRequest() {
        super(OrderType.GIFT_CASH_IN);
    }

    public Long getAmountPerWalletInCents() {
        return amountPerWalletInCents;
    }

    public void setAmountPerWalletInCents(Long amountPerWalletInCents) {
        this.amountPerWalletInCents = amountPerWalletInCents;
    }

    public List<String> getCustomersWalletIds() {
        return customersWalletIds;
    }

    public void setCustomersWalletIds(List<String> customersWalletIds) {
        this.customersWalletIds = customersWalletIds;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("amountPerWalletInCents", amountPerWalletInCents)
                .append("customersWalletIds", customersWalletIds)
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
