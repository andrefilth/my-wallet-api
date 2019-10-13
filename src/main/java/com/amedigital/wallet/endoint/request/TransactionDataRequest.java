package com.amedigital.wallet.endoint.request;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;

import java.time.ZonedDateTime;

public class TransactionDataRequest {

    private OrderStatus status;
    private OrderType type;
    private String walletId;
    private ZonedDateTime dateStart;
    private ZonedDateTime dateEnd;

    private TransactionDataRequest(OrderStatus status,
                                   OrderType type,
                                   String walletId,
                                   ZonedDateTime dateStart,
                                   ZonedDateTime dateEnd) {
        this.status = status;
        this.type = type;
        this.walletId = walletId;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }


    public OrderStatus getStatus() {
        return status;
    }

    public OrderType getType() {
        return type;
    }

    public String getWalletId() {
        return walletId;
    }

    public ZonedDateTime getDateStart() {
        return dateStart;
    }

    public ZonedDateTime getDateEnd() {
        return dateEnd;
    }

    public static TransactionDataRequestBuilder builder() {
        return new TransactionDataRequestBuilder();
    }

    public static final class TransactionDataRequestBuilder {
        private OrderStatus status;
        private OrderType type;
        private String walletId;
        private ZonedDateTime dateStart;
        private ZonedDateTime dateEnd;

        private TransactionDataRequestBuilder() {
        }

        public TransactionDataRequestBuilder setStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public TransactionDataRequestBuilder setType(OrderType type) {
            this.type = type;
            return this;
        }

        public TransactionDataRequestBuilder setWalletId(String walletId) {
            this.walletId = walletId;
            return this;
        }

        public TransactionDataRequestBuilder setDateStart(ZonedDateTime dateStart) {
            this.dateStart = dateStart;
            return this;
        }

        public TransactionDataRequestBuilder setDateEnd(ZonedDateTime dateEnd) {
            this.dateEnd = dateEnd;
            return this;
        }

        public TransactionDataRequest build() {
            return new TransactionDataRequest(status, type, walletId, dateStart, dateEnd);
        }
    }
}
