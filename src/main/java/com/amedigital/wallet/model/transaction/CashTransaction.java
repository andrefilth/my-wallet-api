package com.amedigital.wallet.model.transaction;

import com.amedigital.wallet.constants.enuns.CashStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;

import static com.amedigital.wallet.constants.enuns.PaymentMethod.CASH;

public class CashTransaction extends Transaction {

    private final Long id;
    private final CashStatus cashStatus;
    private final ZonedDateTime cashCreatedAt;
    private final ZonedDateTime cashUpdatedAt;

    private CashTransaction(Builder builder) {
        super(builder);
        this.id = builder.id;
        this.cashStatus = builder.cashStatus;
        this.cashCreatedAt = builder.cashCreatedAt;
        this.cashUpdatedAt = builder.cashUpdatedAt;
    }

    public Long getId() {
        return id;
    }

    public CashStatus getCashStatus() {
        return cashStatus;
    }

    public ZonedDateTime getCashCreatedAt() {
        return cashCreatedAt;
    }

    public ZonedDateTime getCashUpdatedAt() {
        return cashUpdatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder()
                .setId(id)
                .setTransactionId(transactionId)
                .setWalletId(walletId)
                .setWalletUuid(walletUuid)
                .setUuid(uuid)
                .setOrderUuid(orderUuid)
                .setStatus(status)
                .setType(type)
                .setAmountInCents(amountInCents)
                .setTakeRate(takeRate)
                .setReleaseDate(releaseDate)
                .setLatest(latest)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt)
                .setCashStatus(cashStatus)
                .setPeerWalletId(peerWalletId)
                .setPeerWalletUuid(peerWalletUuid)
                .setPeerTransactionUuid(peerTransactionUuid)
                .setCashCreatedAt(createdAt)
                .setCashUpdatedAt(cashUpdatedAt)
                .setReleaseTime(releaseTime)
                .setReleaseTimeUnit(releaseTimeUnit)

                .setTakeRateUnit(takeRateUnit)
                .setGrossAmountInCents(grossAmountInCents)
                .setNetAmountInCents(netAmountInCents)
                .setTakeRateAmountInCents(takeRateAmountInCents)
                ;

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("cashStatus", cashStatus)
                .append("cashCreatedAt", cashCreatedAt)
                .append("cashUpdatedAt", cashUpdatedAt)
                .append("transactionId", transactionId)
                .append("uuid", uuid)
                .append("walletId", walletId)
                .append("walletUuid", walletUuid)
                .append("orderUuid", orderUuid)
                .append("status", status)
                .append("type", type)
                .append("paymentMethod", paymentMethod)
                .append("amountInCents", amountInCents)
                .append("latest", latest)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("peerWalletId", peerWalletId)
                .append("peerWalletUuid", peerWalletUuid)
                .append("peerTransactionUuid", peerTransactionUuid)
                .append("takeRate", takeRate)
                .append("releaseDate", releaseDate)
                .append("releaseTime", releaseTime)
                .append("takeRateUnit", takeRateUnit)
                .append("releaseTimeUnit", releaseTimeUnit)
                .append("takeRateAmountInCents", takeRateAmountInCents)
                .append("netAmountInCents", netAmountInCents)
                .append("grossAmountInCents", grossAmountInCents)
                .toString();
    }

    public static final class Builder extends Transaction.TransactionBuilder<CashTransaction.Builder, CashTransaction> {

        private Long id;
        private CashStatus cashStatus;
        private ZonedDateTime cashCreatedAt;
        private ZonedDateTime cashUpdatedAt;

        public Builder() {
            super(CASH);
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setAmountInCents(Long amountInCents) {
            this.amountInCents = amountInCents;
            return this;
        }

        public Builder setCashStatus(CashStatus cashStatus) {
            this.cashStatus = cashStatus;
            return this;
        }

        public Builder setCashCreatedAt(ZonedDateTime cashCreatedAt) {
            this.cashCreatedAt = cashCreatedAt;
            return this;
        }

        public Builder setCashUpdatedAt(ZonedDateTime cashUpdatedAt) {
            this.cashUpdatedAt = cashUpdatedAt;
            return this;
        }

        public CashTransaction build() {
            return new CashTransaction(this);
        }
    }
}