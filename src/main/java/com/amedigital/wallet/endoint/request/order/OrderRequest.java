package com.amedigital.wallet.endoint.request.order;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.endoint.request.method.MethodRequest;
import com.amedigital.wallet.model.transaction.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PurchaseOrderRequest.class, name = "PURCHASE"),
        @JsonSubTypes.Type(value = CashInOrderRequest.class, name = "CASH_IN"),
        @JsonSubTypes.Type(value = TransferBetweenWalletsOrderRequest.class, name = "TRANSFER_BETWEEN_WALLETS"),
        @JsonSubTypes.Type(value = ReleaseOrderRequest.class, name = "RELEASE"),
        @JsonSubTypes.Type(value = RefundOrderRequest.class, name = "REFUND"),
        @JsonSubTypes.Type(value = CashBackOrderRequest.class, name = "CASH_BACK"),
        @JsonSubTypes.Type(value = GiftCashInOrderRequest.class, name = "GIFT_CASH_IN"),
        @JsonSubTypes.Type(value = CashOutOrderRequest.class, name = "CASH_OUT"),
        @JsonSubTypes.Type(value = StoreCashInOrderRequest.class, name = "STORE_CASH_IN"),
        @JsonSubTypes.Type(value = StoreCashOutOrderRequest.class, name = "STORE_CASH_OUT"),
        @JsonSubTypes.Type(value = BankCashInOrderRequest.class, name = "BANK_CASH_IN")
})
public abstract class OrderRequest {

    protected String createdByWalletId;
    protected OrderType type;
    protected String title;
    protected String description;
    protected Long totalAmountInCents;
    protected List<MethodRequest> paymentMethods;
    protected Map<String, Object> customPayload;
    protected String creditWalletId;
    protected String debitWalletId;

    public OrderRequest(OrderType type) {
        this.type = type;
    }

    public String getCreatedByWalletId() {
        return createdByWalletId;
    }

    public void setCreatedByWalletId(String createdByWalletId) {
        this.createdByWalletId = createdByWalletId;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTotalAmountInCents() {
        return totalAmountInCents;
    }

    public void setTotalAmountInCents(Long totalAmountInCents) {
        this.totalAmountInCents = totalAmountInCents;
    }

    public List<MethodRequest> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<MethodRequest> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    @JsonIgnore
    public List<Transaction> transactions(Function<MethodRequest, Transaction> converterFunction) {
        return paymentMethods.stream().map(converterFunction).collect(toList());
    }

    public Map<String, Object> getCustomPayload() {
        return customPayload != null ? customPayload : Collections.emptyMap();
    }

    public void setCustomPayload(Map<String, Object> customPayload) {
        this.customPayload = customPayload;
    }

    public String getCreditWalletId() {
        return creditWalletId;
    }

    public void setCreditWalletId(String creditWalletId) {
        this.creditWalletId = creditWalletId;
    }

    public String getDebitWalletId() {
        return debitWalletId;
    }

    public void setDebitWalletId(String debitWalletId) {
        this.debitWalletId = debitWalletId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
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