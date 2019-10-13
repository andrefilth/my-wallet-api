package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.model.StatementItem;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;
import java.util.List;

public class StatementItemResponse {

    private String id;
    private OrderType type;
    private TransactionType transactionType;
    private String title;
    private String description;
    private OrderStatus status;
    private Long amountInCents;
    private Long calculatedAmount;
    private List<PaymentMethod> paymentMethods;
    private String orderReference;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private WalletReferenceResponse walletReference;
    private List<RuleResponse> rules;

    public StatementItemResponse(StatementItem statementItem) {
        OwnerReferenceResponse ownerReferenceResponse = new OwnerReferenceResponse(statementItem.getWalletReference().getOwner().getName());
        this.id = statementItem.getId();
        this.type = statementItem.getType();
        this.transactionType = statementItem.getTransactionType();
        this.title = statementItem.getTitle();
        this.description = statementItem.getDescription();
        this.status = statementItem.getStatus();
        this.amountInCents = statementItem.getAmountInCents();
        this.paymentMethods = statementItem.getPaymentMethods();
        this.orderReference = statementItem.getOrderReference();
        this.createdAt = statementItem.getCreatedAt();
        this.updatedAt = statementItem.getUpdatedAt();
        this.walletReference = new WalletReferenceResponse(statementItem.getWalletReference().getType(), ownerReferenceResponse);

        this.calculatedAmount = statementItem.getNetAmountInCents();
        this.rules = Lists.newArrayList(new RuleResponse());

    }

    public String getId() {
        return id;
    }

    public OrderType getType() {
        return type;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public WalletReferenceResponse getWalletReference() {
        return walletReference;
    }

    public Long getCalculatedAmount() {
        return calculatedAmount;
    }

    public List<RuleResponse> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("type", type)
                .append("transactionType", transactionType)
                .append("title", title)
                .append("description", description)
                .append("status", status)
                .append("amountInCents", amountInCents)
                .append("paymentMethods", paymentMethods)
                .append("orderReference", orderReference)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("walletReference", walletReference)
                .build();
    }
}
