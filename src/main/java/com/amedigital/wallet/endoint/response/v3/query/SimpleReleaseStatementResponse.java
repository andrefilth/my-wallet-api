package com.amedigital.wallet.endoint.response.v3.query;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;

import java.time.ZonedDateTime;

public class SimpleReleaseStatementResponse {


    private String orderUuid;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private String referenceOrderUuid;
    private String secondaryId;
    private String transactionUuid;
    private ZonedDateTime transactionReleaseDate;


    public SimpleReleaseStatementResponse(String orderUuid, OrderType orderType, OrderStatus orderStatus, String referenceOrderUuid, String secondaryId, String transactionUuid, ZonedDateTime transactionReleaseDate) {
        this.orderUuid = orderUuid;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.referenceOrderUuid = referenceOrderUuid;
        this.secondaryId = secondaryId;
        this.transactionUuid = transactionUuid;
        this.transactionReleaseDate = transactionReleaseDate;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getReferenceOrderUuid() {
        return referenceOrderUuid;
    }

    public void setReferenceOrderUuid(String referenceOrderUuid) {
        this.referenceOrderUuid = referenceOrderUuid;
    }

    public String getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    public String getTransactionUuid() {
        return transactionUuid;
    }

    public void setTransactionUuid(String transactionUuid) {
        this.transactionUuid = transactionUuid;
    }

    public ZonedDateTime getTransactionReleaseDate() {
        return transactionReleaseDate;
    }

    public void setTransactionReleaseDate(ZonedDateTime transactionReleaseDate) {
        this.transactionReleaseDate = transactionReleaseDate;
    }


}
