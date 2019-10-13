package com.amedigital.wallet.model;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.constants.enuns.WalletType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderItem {

    private final String id;
    private final OrderType type;
    private final OrderStatus status;
    private final String title;
    private final String description;
    private final Long totalAmountInCents;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final String nsu;
    private final TransactionType transactionType;
    private final String transactionWalletId;
    private final String transactionPeerWalletId;
    private final String referenceOrderUuid;
    private final WalletType peerWalletType;
    private final String peerOwnerName;
    private final String walletId;
    private final String peerWalletId;
    private final List<String> paymentMethods;
    private final String secondaryId;

    private OrderItem(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.status = builder.status;
        this.title = builder.title;
        this.description = builder.description;
        this.totalAmountInCents = builder.totalAmountInCents;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.nsu = builder.nsu;
        this.transactionType = builder.transactionType;
        this.transactionWalletId = builder.transactionWalletId;
        this.transactionPeerWalletId = builder.transactionPeerWalletId;
        this.referenceOrderUuid = builder.referenceOrderUuid;
        this.peerWalletType = builder.peerWalletType;
        this.peerOwnerName = builder.peerOwnerName;
        this.walletId = builder.walletId;
        this.peerWalletId = builder.peerWalletId;
        this.paymentMethods = builder.paymentMethods;
        this.secondaryId = builder.secondaryId;
    }

    public String getId() {
        return id;
    }

    public OrderType getType() {
        return type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getTotalAmountInCents() {
        return totalAmountInCents;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getNsu() {
        return nsu;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getTransactionWalletId() {
        return transactionWalletId;
    }

    public String getTransactionPeerWalletId() {
        return transactionPeerWalletId;
    }

    public String getReferenceOrderUuid() {
        return referenceOrderUuid;
    }

    public WalletType getPeerWalletType() {
        return peerWalletType;
    }

    public String getPeerOwnerName() {
        return peerOwnerName;
    }

    public String getWalletId() {
        return walletId;
    }

    public String getPeerWalletId() {
        return peerWalletId;
    }

    public List<String> getPaymentMethods() {
        return paymentMethods;
    }

    public String getSecondaryId() {
        return secondaryId;
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
                .append("peerOwnerName", peerOwnerName)
                .append("walletId", walletId)
                .append("peerWalletId", peerWalletId)
                .append("paymentMethods", paymentMethods)
                .append("secondaryId", secondaryId)
                .toString();
    }

    public static final class Builder {
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

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setType(OrderType type) {
            this.type = type;
            return this;
        }

        public Builder setStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setTotalAmountInCents(Long totalAmountInCents) {
            this.totalAmountInCents = totalAmountInCents;
            return this;
        }

        public Builder setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setNsu(String nsu) {
            this.nsu = nsu;
            return this;
        }

        public Builder setTransactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder setTransactionWalletId(String transactionWalletId) {
            this.transactionWalletId = transactionWalletId;
            return this;
        }

        public Builder setTransactionPeerWalletId(String transactionPeerWalletId) {
            this.transactionPeerWalletId = transactionPeerWalletId;
            return this;
        }

        public Builder setReferenceOrderUuid(String referenceOrderUuid) {
            this.referenceOrderUuid = referenceOrderUuid;
            return this;
        }

        public Builder setPeerWalletType(WalletType peerWalletType) {
            this.peerWalletType = peerWalletType;
            return this;
        }

        public Builder setPeerOwnerName(String peerOwnerName) {
            this.peerOwnerName = peerOwnerName;
            return this;
        }

        public Builder setWalletId(String walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder setPeerWalletId(String peerWalletId) {
            this.peerWalletId = peerWalletId;
            return this;
        }

        public Builder setPaymentMethods(List<String> paymentMethods) {
            this.paymentMethods = paymentMethods;
            return this;
        }

        public Builder setSecondaryId(String secondaryId) {
            this.secondaryId = secondaryId;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
