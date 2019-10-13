package com.amedigital.wallet.model.transaction;

import com.amedigital.wallet.commons.Build;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TakeRateUnit;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.util.MoneyUtil;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public abstract class Transaction {

    protected final Long transactionId;
    protected final String uuid;
    protected final Long walletId;
    protected final String walletUuid;
    protected final String orderUuid;
    protected final TransactionStatus status;
    protected final TransactionType type;
    protected final PaymentMethod paymentMethod;
    protected final boolean latest;
    protected final ZonedDateTime createdAt;
    protected final ZonedDateTime updatedAt;
    protected final Long peerWalletId;
    protected final String peerWalletUuid;
    protected final String peerTransactionUuid;

    protected final Long takeRate;

    /**
     * Valor em centavos da taxa que será aplicada a transação (takeRate * amountInCents da transacao de referencia)
     */
    protected final Long takeRateAmountInCents;

    /**
     * Valor bruto da transação (takeRateAmountInCents + netAmountInCents)
     */
    protected final Long grossAmountInCents;

    /**
     * Valor liquido da transação (amountInCents - takeRateAmountInCents)
     */
    protected final Long netAmountInCents;

    /**
     * Valor liquido da transação que será considerado para o saldo da carteira.
     */
    protected final Long amountInCents;

    protected final ZonedDateTime releaseDate;

    protected final Long releaseTime;
    protected final TakeRateUnit takeRateUnit;
    protected final ChronoUnit releaseTimeUnit;

    protected Transaction(TransactionBuilder transactionBuilder) {
        this.transactionId = transactionBuilder.transactionId;
        this.uuid = transactionBuilder.uuid;
        this.walletId = transactionBuilder.walletId;
        this.orderUuid = transactionBuilder.orderUuid;
        this.status = transactionBuilder.status;
        this.type = transactionBuilder.type;
        this.paymentMethod = transactionBuilder.paymentMethod;
        this.amountInCents = transactionBuilder.amountInCents;
        this.takeRate = transactionBuilder.takeRate;
        this.releaseDate = transactionBuilder.releaseDate;
        this.latest = transactionBuilder.latest;
        this.createdAt = transactionBuilder.createdAt;
        this.updatedAt = transactionBuilder.updatedAt;
        this.peerWalletId = transactionBuilder.peerWalletId;
        this.peerTransactionUuid = transactionBuilder.peerTransactionUuid;
        this.walletUuid = transactionBuilder.walletUuid;
        this.peerWalletUuid = transactionBuilder.peerWalletUuid;
        this.releaseTime = transactionBuilder.releaseTime;
        this.releaseTimeUnit = transactionBuilder.releaseTimeUnit;
        this.takeRateUnit = transactionBuilder.takeRateUnit;
        this.grossAmountInCents = transactionBuilder.grossAmountInCents;
        this.takeRateAmountInCents = transactionBuilder.takeRateAmountInCents;
        this.netAmountInCents = transactionBuilder.netAmountInCents;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getUuid() {
        return uuid;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public TransactionType getType() {
        return type;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public Long getTakeRate() {
        return takeRate;
    }

    public ZonedDateTime getReleaseDate() {
        return releaseDate;
    }

    public boolean isLatest() {
        return latest;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getWalletId() {
        return walletId;
    }

    public Long getPeerWalletId() {
        return peerWalletId;
    }

    public String getPeerTransactionUuid() {
        return peerTransactionUuid;
    }

    public boolean isCaptured() {
        return TransactionStatus.CAPTURED.equals(this.status);
    }

    public String getWalletUuid() {
        return walletUuid;
    }

    public String getPeerWalletUuid() {
        return peerWalletUuid;
    }

	public Long getTakeRateAmountInCents() {
		return takeRateAmountInCents;
	}

	public Long getReleaseTime() {
		return releaseTime;
	}

	public ChronoUnit getReleaseTimeUnit() {
		return releaseTimeUnit;
	}

    public TakeRateUnit getTakeRateUnit() {
        return takeRateUnit;
    }

    public Long getGrossAmountInCents() {
        return grossAmountInCents;
    }

    public Long getNetAmountInCents() {
        return netAmountInCents;
    }

    public boolean hasRules() {
        return takeRate != null && takeRateUnit != null && releaseTime != null;
    }

    public abstract TransactionBuilder copy();

    public abstract static class TransactionBuilder<B extends TransactionBuilder, T extends Transaction> implements Build<T> {

        protected final PaymentMethod paymentMethod;

        protected Long transactionId;
        protected String uuid;
        protected Long walletId;
        protected String walletUuid;
        protected String orderUuid;
        protected TransactionStatus status;
        protected TransactionType type;
        protected Long amountInCents;
        protected Long takeRate;
        protected ZonedDateTime releaseDate;
        protected boolean latest = true;
        protected ZonedDateTime createdAt;
        protected ZonedDateTime updatedAt;
        protected Long peerWalletId;
        protected String peerWalletUuid;
        protected String peerTransactionUuid;
        protected Long releaseTime;
        protected ChronoUnit releaseTimeUnit;
        protected TakeRateUnit takeRateUnit;
        protected Long grossAmountInCents;
        protected Long takeRateAmountInCents;
        protected Long netAmountInCents;


        public B setTransactionId(Long transactionId) {
            this.transactionId = transactionId;
            return (B) this;
        }

        public B setOrderUuid(String orderUuid) {
            this.orderUuid = orderUuid;
            return (B) this;
        }

        public B setUuid(String uuid) {
            this.uuid = uuid;
            return (B) this;
        }

        public B setWalletId(Long walletId) {
            this.walletId = walletId;
            return (B) this;
        }

        public B setStatus(TransactionStatus status) {
            this.status = status;
            return (B) this;
        }

        public B setType(TransactionType type) {
            this.type = type;
            return (B) this;
        }

        public B setAmountInCents(Long amountInCents) {
            this.amountInCents = amountInCents;
            return (B) this;
        }

        public B setTakeRate(Long takeRate) {
            this.takeRate = takeRate;
            return (B) this;
        }

        public B setReleaseDate(ZonedDateTime releaseDate) {
            this.releaseDate = releaseDate;
            return (B) this;
        }

        public B setLatest(boolean latest) {
            this.latest = latest;
            return (B) this;
        }

        public B setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return (B) this;
        }

        public B setUpdatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return (B) this;
        }

        public B setPeerWalletId(Long peerWalletId) {
            this.peerWalletId = peerWalletId;
            return (B) this;
        }

        public B setPeerTransactionUuid(String peerTransactionUuid) {
            this.peerTransactionUuid = peerTransactionUuid;
            return (B) this;
        }

        public B setWalletUuid(String walletUuid) {
            this.walletUuid = walletUuid;
            return (B) this;
        }

        public B setPeerWalletUuid(String peerWalletUuid) {
            this.peerWalletUuid = peerWalletUuid;
            return (B) this;
        }
        
        public B setReleaseTimeUnit(ChronoUnit releaseTimeUnit) {
        	this.releaseTimeUnit = releaseTimeUnit;
        	return (B) this;
        }
        
        public B setReleaseTime(Long releaseTime) {
        	this.releaseTime = releaseTime;
        	return (B) this;
        }

        public B setTakeRateUnit(TakeRateUnit takeRateUnit) {
            this.takeRateUnit = takeRateUnit;
            return (B) this;
        }

        protected TransactionBuilder(PaymentMethod paymentMethod) {
        	this.paymentMethod = paymentMethod;
        }

        public B setGrossAmountInCents(Long grossAmountInCents) {
            this.grossAmountInCents = grossAmountInCents;
            return (B) this;
        }

        public B setTakeRateAmountInCents(Long takeRateAmountInCents) {
            this.takeRateAmountInCents = takeRateAmountInCents;
            return (B) this;
        }

        public B setNetAmountInCents(Long netAmountInCents) {
            this.netAmountInCents = netAmountInCents;
            return (B) this;
        }
    }

}
