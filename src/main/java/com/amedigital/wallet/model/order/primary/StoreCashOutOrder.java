package com.amedigital.wallet.model.order.primary;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.PrimaryOrder;

public class StoreCashOutOrder extends PrimaryOrder {

	private final Long creditWalletId;
	private final String creditWalletUUID;
	private final Long debitWalletId;
	private final String debitWalletUUID;
	
    private StoreCashOutOrder(Builder builder) {
        super(builder);
        this.creditWalletId = builder.creditWalletId;
        this.creditWalletUUID = builder.creditWalletUUID;
        this.debitWalletId = builder.debitWalletId;
        this.debitWalletUUID = builder.debitWalletUUID;
    }

    public Long getCreditWalletId() {
        return creditWalletId;
    }
    
    public String getCreditWalletUUID() {
    	return creditWalletUUID;
    }
    
    public Long getDebitWalletId() {
    	return debitWalletId;
    }
    
    public String getDebitWalletUUID() {
    	return debitWalletUUID;
    }
    
    @Override
    public Builder copy() {
        return new StoreCashOutOrder.Builder()
                .setId(id)
                .setUuid(uuid)
                .setNsu(nsu)
                .setStatus(status)
                .setAction(action)
                .setTotalAmountInCents(totalAmountInCents)
                .setTitle(title)
                .setDescription(description)
                .setOrderDetailUuid(orderDetailUuid)
                .setAuthorizationMethod(authorizationMethod)
                .setTransactions(transactions)
                .setCreatedByWalletId(createdByWalletId)
                .setUpdatedAt(updatedAt)
                .setCreatedAt(createdAt)
                .setCustomPayload(customPayload)
                .setReferenceOrderUuid(referenceOrderUuid)
                .setCreatedByOwner(createdByOwner)
                .setCreditWalletId(creditWalletId)
                .setCreditWalletUUID(creditWalletUUID)
                .setDebitWalletId(debitWalletId)
                .setDebitWalletUUID(debitWalletUUID)
                .setPaymentMethods(paymentMethods);
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
                .append("creditWalletId", creditWalletId)
                .append("creditWalletUUID", creditWalletUUID)
                .append("debitWalletId", debitWalletId)
                .append("debitWalletUUID", debitWalletUUID)
                .build();
    }

    public static class Builder extends PrimaryOrder.Builder<StoreCashOutOrder.Builder, StoreCashOutOrder> {

    	public Long creditWalletId;
    	public String creditWalletUUID;
    	public Long debitWalletId;
    	public String debitWalletUUID;
    	
        public Builder() {
            super(OrderType.STORE_CASH_OUT);
        }

        public Builder setCreditWalletId(Long creditWalletId) {
            this.creditWalletId = creditWalletId;
            return this;
        }
        
        public Builder setCreditWalletUUID(String creditWalletUUID) {
        	this.creditWalletUUID = creditWalletUUID;
        	return this;
        }
        
        public Builder setDebitWalletId(Long debitWalletId) {
        	this.debitWalletId = debitWalletId;
        	return this;
        }
        
        public Builder setDebitWalletUUID(String debitWalletUUID) {
        	this.debitWalletUUID = debitWalletUUID;
        	return this;
        }
        
        @Override
        public StoreCashOutOrder build() {
            return new StoreCashOutOrder(this);
        }
    }
}
