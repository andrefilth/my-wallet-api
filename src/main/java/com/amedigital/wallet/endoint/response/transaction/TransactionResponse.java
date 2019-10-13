package com.amedigital.wallet.endoint.response.transaction;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TakeRateUnit;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.endoint.response.RuleResponse;
import com.amedigital.wallet.model.transaction.Transaction;

import java.time.ZonedDateTime;

public abstract class TransactionResponse {

    protected String id;
    protected TransactionType type;
    protected PaymentMethod paymentMethod;
    protected TakeRateUnit takeRateUnit;
    protected Long takeRateAmountInCents;
    protected Long amountInCents;
    protected Long grossAmountInCents;
    protected Long netAmountInCents;
    protected TransactionStatus status;
    protected ZonedDateTime createdAt;
    protected ZonedDateTime updatedAt;
    protected RuleResponse rule;
    protected String walletId;
    protected String peerWalletId;


    protected TransactionResponse(Transaction transaction) {
        this.id = transaction.getUuid();
        this.type = transaction.getType();
        this.paymentMethod = transaction.getPaymentMethod();
        this.amountInCents = transaction.getAmountInCents();
        this.status = transaction.getStatus();
        this.createdAt = transaction.getCreatedAt();
        this.updatedAt = transaction.getUpdatedAt();
        this.walletId = transaction.getWalletUuid();
        this.peerWalletId = transaction.getPeerWalletUuid();
        this.takeRateAmountInCents = transaction.getTakeRateAmountInCents();
        this.grossAmountInCents = transaction.getGrossAmountInCents();
        this.netAmountInCents = transaction.getNetAmountInCents();
        this.takeRateAmountInCents = transaction.getTakeRateAmountInCents();
        this.takeRateUnit = transaction.getTakeRateUnit();

        if(transaction.hasRules())
            this.rule = new RuleResponse(transaction.getPaymentMethod(), transaction.getTakeRate(), transaction.getTakeRateUnit(), transaction.getReleaseTime(), transaction.getReleaseTimeUnit());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getNetAmountInCents() {
        return netAmountInCents;
    }

    public void setNetAmountInCents(Long netAmountInCents) {
        this.netAmountInCents = netAmountInCents;
    }

    public Long getTakeRateAmountInCents() {
        return takeRateAmountInCents;
    }

    public void setTakeRateAmountInCents(Long takeRateAmountInCents) {
        this.takeRateAmountInCents = takeRateAmountInCents;
    }

    public RuleResponse getRule() {
        return rule;
    }

    public void setRule(RuleResponse rule) {
        this.rule = rule;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getPeerWalletId() {
        return peerWalletId;
    }

    public void setPeerWalletId(String peerWalletId) {
        this.peerWalletId = peerWalletId;
    }


    public TakeRateUnit getTakeRateUnit() {
        return takeRateUnit;
    }

    public void setTakeRateUnit(TakeRateUnit takeRateUnit) {
        this.takeRateUnit = takeRateUnit;
    }

    public Long getGrossAmountInCents() {
        return grossAmountInCents;
    }

    public void setGrossAmountInCents(Long grossAmountInCents) {
        this.grossAmountInCents = grossAmountInCents;
    }


}

