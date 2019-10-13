package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.constants.enuns.WalletType;
import com.amedigital.wallet.model.OrderItem;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderItemResponse {

    private String id;
    private OrderType type;
    private OrderStatus status;
    private String title;
    private String description;
    private Long totalAmountInCents;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String nsu;
    private TransactionType transactionType;
    private String transactionWalletId;
    private String transactionPeerWalletId;
    private String referenceOrderUuid;
    private WalletType peerWalletType;
    private String peerOwnerName;
    private String walletId;
    private String peerWalletId;
    private List<String> paymentMethods;
    private String secondaryId;

    public OrderItemResponse(OrderItem item) {
        this.id = item.getId();
        this.type = item.getType();
        this.status = item.getStatus();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.totalAmountInCents = item.getTotalAmountInCents();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
        this.nsu = item.getNsu();
        this.transactionType = item.getTransactionType();
        this.transactionWalletId = item.getTransactionWalletId();
        this.transactionPeerWalletId = item.getTransactionPeerWalletId();
        this.referenceOrderUuid = item.getReferenceOrderUuid();
        this.peerWalletType = item.getPeerWalletType();
        this.peerOwnerName = item.getPeerOwnerName();
        this.walletId = item.getWalletId();
        this.peerWalletId = item.getPeerWalletId();
        this.paymentMethods = item.getPaymentMethods();
        this.secondaryId = item.getSecondaryId();
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getSecondaryId() {
        return secondaryId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNsu() {
        return nsu;
    }

    public void setNsu(String nsu) {
        this.nsu = nsu;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionWalletId() {
        return transactionWalletId;
    }

    public void setTransactionWalletId(String transactionWalletId) {
        this.transactionWalletId = transactionWalletId;
    }

    public String getTransactionPeerWalletId() {
        return transactionPeerWalletId;
    }

    public void setTransactionPeerWalletId(String transactionPeerWalletId) {
        this.transactionPeerWalletId = transactionPeerWalletId;
    }

    public String getReferenceOrderUuid() {
        return referenceOrderUuid;
    }

    public void setReferenceOrderUuid(String referenceOrderUuid) {
        this.referenceOrderUuid = referenceOrderUuid;
    }

    public WalletType getPeerWalletType() {
        return peerWalletType;
    }

    public void setPeerWalletType(WalletType peerWalletType) {
        this.peerWalletType = peerWalletType;
    }

    public String getPeerOwnerName() {
        return peerOwnerName;
    }

    public void setPeerOwnerName(String peerOwnerName) {
        this.peerOwnerName = peerOwnerName;
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

    public List<String> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<String> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("type", type)
                .append("status", status)
                .append("title", title)
                .append("description", description)
                .append("totalAmountInCents", totalAmountInCents)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("nsu", nsu)
                .append("transactionType", transactionType)
                .append("transactionWalletId", transactionWalletId)
                .append("transactionPeerWalletId", transactionPeerWalletId)
                .append("referenceOrderUuid", referenceOrderUuid)
                .append("peerWalletType", peerWalletType)
                .append("walletId", walletId)
                .append("peerWalletId", peerWalletId)
                .append("paymentMethods", paymentMethods)
                .append("secondaryId", secondaryId)
                .build();
    }

}
