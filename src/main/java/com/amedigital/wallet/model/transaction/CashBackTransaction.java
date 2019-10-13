package com.amedigital.wallet.model.transaction;

import com.amedigital.wallet.constants.enuns.CashBackStatus;
import com.amedigital.wallet.constants.enuns.CashStatus;
import com.amedigital.wallet.constants.enuns.PaymentMethod;

import java.time.ZonedDateTime;

public class CashBackTransaction extends Transaction {

    private final Long id;
    private final CashBackStatus cashStatus;
    private final ZonedDateTime cashCreatedAt;
    private final ZonedDateTime cashUpdatedAt;

    private CashBackTransaction(CashBackTransaction.Builder builder) {
        super(builder);

        this.id = builder.id;
        this.cashStatus = builder.cashStatus;
        this.cashCreatedAt = builder.cashCreatedAt;
        this.cashUpdatedAt = builder.cashUpdatedAt;
    }

    public Long getId() {
        return id;
    }

    public CashBackStatus getCashStatus() {
        return cashStatus;
    }

    public ZonedDateTime getCashCreatedAt() {
        return cashCreatedAt;
    }

    public ZonedDateTime getCashUpdatedAt() {
        return cashUpdatedAt;
    }

    public static CashBackTransaction.Builder builder() {
        return new CashBackTransaction.Builder();
    }

    public CashBackTransaction.Builder copy() {
        return new CashBackTransaction.Builder()
                .setId(id)
                .setTransactionId(transactionId)
                .setWalletId(walletId)
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
                .setPeerTransactionUuid(peerTransactionUuid)
                .setCashCreatedAt(createdAt)
                .setCashUpdatedAt(cashUpdatedAt)
                .setReleaseTime(releaseTime)
                .setReleaseTimeUnit(releaseTimeUnit)

                .setTakeRateUnit(takeRateUnit)
                .setGrossAmountInCents(grossAmountInCents)
                .setNetAmountInCents(netAmountInCents)
                .setTakeRateAmountInCents(takeRateAmountInCents);
    }

    public static final class Builder extends Transaction.TransactionBuilder<CashBackTransaction.Builder, CashBackTransaction> {

        private Long id;
        private CashBackStatus cashStatus;
        private ZonedDateTime cashCreatedAt;
        private ZonedDateTime cashUpdatedAt;

        public Builder() {
            super(PaymentMethod.CASH_BACK);
        }

        public CashBackTransaction.Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public CashBackTransaction.Builder setAmountInCents(Long amountInCents) {
            this.amountInCents = amountInCents;
            return this;
        }

        public CashBackTransaction.Builder setCashStatus(CashBackStatus cashStatus) {
            this.cashStatus = cashStatus;
            return this;
        }

        public CashBackTransaction.Builder setCashCreatedAt(ZonedDateTime cashCreatedAt) {
            this.cashCreatedAt = cashCreatedAt;
            return this;
        }

        public CashBackTransaction.Builder setCashUpdatedAt(ZonedDateTime cashUpdatedAt) {
            this.cashUpdatedAt = cashUpdatedAt;
            return this;
        }

        public CashBackTransaction build() {
            return new CashBackTransaction(this);
        }
    }

}
