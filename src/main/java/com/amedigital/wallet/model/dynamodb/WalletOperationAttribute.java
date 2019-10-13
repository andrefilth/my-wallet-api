package com.amedigital.wallet.model.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@DynamoDBTable(tableName = "wallet_operation_attribute")
public class WalletOperationAttribute {

    private static final ObjectMapper mapper = new ObjectMapper();

    @DynamoDBHashKey(attributeName = "id")
    private String id;

    @DynamoDBAttribute(attributeName = "cashbackAmountValue")
    private Long cashbackAmountValue;

    @DynamoDBAttribute(attributeName = "orderId")
    private String orderId;

    @DynamoDBAttribute(attributeName = "customPayload")
    @DynamoDBTypeConvertedJson
    private Map<String, Object> customPayload;

    @DynamoDBAttribute(attributeName = "deliveryAddress")
    private String deliveryAddress;

    @DynamoDBAttribute(attributeName = "transactionChangedCallbackUrl")
    private String transactionChangedCallbackUrl;

    @DynamoDBAttribute(attributeName = "externalOrder")
    private AttributeExternalOrder externalOrder;

    @DynamoDBAttribute(attributeName = "itens")
    private List<AttributeItem> itens;


    public WalletOperationAttribute() {

    }

    private WalletOperationAttribute(Builder builder) {
        this.id = builder.id;
        this.orderId = builder.orderId;
        this.customPayload = builder.customPayload;
        this.cashbackAmountValue = builder.cashbackAmountValue;
        this.deliveryAddress = builder.deliveryAddress;
        this.transactionChangedCallbackUrl = builder.transactionChangedCallbackUrl;
        this.externalOrder = builder.externalOrder;
        this.itens = builder.itens;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public Map<String, Object> getCustomPayload() {
        return customPayload;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCustomPayload(Map<String, Object> customPayload) {
        this.customPayload = customPayload;
    }

    public Long getCashbackAmountValue() {
        return cashbackAmountValue;
    }

    public void setCashbackAmountValue(Long cashbackAmountValue) {
        this.cashbackAmountValue = cashbackAmountValue;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getTransactionChangedCallbackUrl() {
        return transactionChangedCallbackUrl;
    }

    public void setTransactionChangedCallbackUrl(String transactionChangedCallbackUrl) {
        this.transactionChangedCallbackUrl = transactionChangedCallbackUrl;
    }

    public AttributeExternalOrder getExternalOrder() {
        return externalOrder;
    }

    public void setExternalOrder(AttributeExternalOrder externalOrder) {
        this.externalOrder = externalOrder;
    }

    public List<AttributeItem> getItens() {
        return itens;
    }

    public void setItens(List<AttributeItem> itens) {
        this.itens = itens;
    }



    public static final class Builder {

        private String id;
        private String orderId;
        private Map<String, Object> customPayload;
        private Long cashbackAmountValue;
        private String deliveryAddress;
        private String transactionChangedCallbackUrl;
        private AttributeExternalOrder externalOrder;
        private List<AttributeItem> itens;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setCustomPayload(Map<String, Object> customPayload) {
            this.customPayload = customPayload;
            return this;
        }

        public Builder setCashbackAmountValue(Long cashbackAmountValue) {
            this.cashbackAmountValue = cashbackAmountValue;
            return this;
        }

        public Builder setDeliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder setTransactionChangedCallbackUrl(String transactionChangedCallbackUrl) {
            this.transactionChangedCallbackUrl = transactionChangedCallbackUrl;
            return this;
        }

        public Builder setExternalOrder(AttributeExternalOrder externalOrder) {
            this.externalOrder = externalOrder;
            return this;
        }

        public Builder setItens(List<AttributeItem> itens) {
            this.itens = itens;
            return this;
        }

        public WalletOperationAttribute build() {
            return new WalletOperationAttribute(this);
        }
    }


}
