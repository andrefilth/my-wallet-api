package com.amedigital.wallet.model.order.primary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.model.order.PrimaryOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GiftCashInOrder extends PrimaryOrder {
    private List<String> customerWalletIds;

    private Long amountPerWalletInCents;

    private GiftCashInOrder(GiftCashInOrder.Builder builder) {
        super(builder);

        this.customerWalletIds = Optional.ofNullable(builder.customerWalletIds).orElse(Collections.emptyList());
        this.amountPerWalletInCents = Optional.ofNullable(builder.amountPerWalletInCents).orElse(0L);
    }

    public List<String> getCustomerWalletIds() {
        return customerWalletIds;
    }

    public long getAmountPerWalletInCents() {
        return amountPerWalletInCents;
    }

    @Override
    public GiftCashInOrder.Builder copy() {
        return new GiftCashInOrder.Builder()
                .setId(id)
                .setUuid(uuid)
                .setNsu(nsu)
                .setTitle(title)
                .setDescription(description)
                .setTotalAmountInCents(Optional.ofNullable(transactions)
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(t -> TransactionType.CREDIT == t.getType())
                        .map(Transaction::getAmountInCents)
                        .reduce(0L, (acc, x) -> x + acc))
                .setTransactions(transactions)
                .setStatus(status)
                .setAction(action)
                .setUpdatedAt(updatedAt)
                .setCreatedAt(createdAt)
                .setCustomPayload(customPayload)
                .setReferenceOrderUuid(referenceOrderUuid)
                .setCreatedByOwner(createdByOwner)
                .setPaymentMethods(paymentMethods)
                .setCustomerWalletIds(customerWalletIds)
                .setAmountPerWalletInCents(amountPerWalletInCents)
                .setOrderDetailUuid(orderDetailUuid)
                .setAuthorizationMethod(authorizationMethod)
                .setReferenceOrderUuid(referenceOrderUuid)
                .setCreatedByWalletId(createdByWalletId);
    }

    public static class Builder extends PrimaryOrder.Builder<GiftCashInOrder.Builder, GiftCashInOrder> {
        List<String> customerWalletIds;
        Long amountPerWalletInCents;

        public Builder() {
            super(OrderType.GIFT_CASH_IN);
        }

        public Builder setCustomerWalletIds(List<String> customerWalletIds) {
            this.customerWalletIds = customerWalletIds;
            return this;
        }

        public Builder setAmountPerWalletInCents(Long amountPerWalletInCents) {
            this.amountPerWalletInCents = amountPerWalletInCents;
            return this;
        }

        @Override
        public GiftCashInOrder build() {
            return new GiftCashInOrder(this);
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
                .append("customerWalletIds", customerWalletIds)
                .append("amountPerWalletInCents", amountPerWalletInCents)
                .build();
    }
}
