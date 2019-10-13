package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.endoint.response.transaction.*;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.transaction.BankTransferTransaction;
import com.amedigital.wallet.model.transaction.CashBackTransaction;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class OrderResponse {

    protected final String id;
    protected final OrderType type;
    protected final OrderStatus status;
    protected final ActionType action;
    protected final String title;
    protected final String description;
    protected final Long totalAmountInCents;
    protected final ZonedDateTime createdAt;
    protected final ZonedDateTime updatedAt;
    protected final TransactionType transactionType;
    protected final List<TransactionResponse> transactions;
    protected final List<PaymentMethod> paymentMethods;
    protected final Map<String, Object> customPayload;
    protected final String nsu;
    protected final WalletResponse createdByWallet;
    protected final OwnerResponse createdByOwner;
    protected final String orderDetailUuid;
    protected final String referenceOrderUuid;

    public OrderResponse(Order order) {
        this.id = order.getUuid();
        this.type = order.getType();
        this.status = order.getStatus();
        this.action = order.getAction().getType();
        this.title = order.getTitle();
        this.description = order.getDescription();
        this.totalAmountInCents = order.getTotalAmountInCents();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        

        this.transactionType = order.getTransactions().stream()
                .filter(t -> TransactionType.DEBIT.equals(t.getType()))
                .findFirst()
                .map(t -> TransactionType.DEBIT)
                .orElse(TransactionType.CREDIT);

        this.transactions = order.getTransactions()
                .stream()
                .map(t -> {
                    if (t instanceof CreditCardTransaction) {
                        return new CreditCardTransactionResponse((CreditCardTransaction) t);
                    } else if (t instanceof CashTransaction) {
                    	return new CashTransactionResponse((CashTransaction) t);
                    } else if (t instanceof BankTransferTransaction) {
                        return new BankTransferTransactionResponse((BankTransferTransaction) t);
                    } else if (t instanceof CashBackTransaction) {
                        return new CashBackTransactionResponse((CashBackTransaction) t);
                    } else {
                    	throw new AmeInternalException("Invalid Transaction Response");
                    }
                })
                .collect(toList());

        this.paymentMethods = order.getPaymentMethods()
                .stream()
                .map(PaymentMethod::valueOf)
                .distinct()
                .collect(toList());

        this.customPayload = order.getCustomPayload();

        this.nsu = order.getNsu();
        this.createdByWallet = order.getCreatedByWallet() != null ? new WalletResponse(order.getCreatedByWallet()) : null;
        this.createdByOwner = new OwnerResponse(order.getCreatedByOwner());
        this.orderDetailUuid = order.getOrderDetailUuid();
        this.referenceOrderUuid = order.getReferenceOrderUuid();
    }

    public String getId() {
        return id;
    }

    public OrderType getType() {
        return type;
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

    public List<TransactionResponse> getTransactions() {
        return transactions;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public ActionType getAction() {
        return action;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public Map<String, Object> getCustomPayload() {
        return customPayload;
    }

    public String getNsu() {
        return nsu;
    }

    public OwnerResponse getCreatedByOwner() {
        return createdByOwner;
    }

    public String getOrderDetailUuid() {
        return orderDetailUuid;
    }

    public String getReferenceOrderUuid() {
        return referenceOrderUuid;
    }

    public WalletResponse getCreatedByWallet() {
        return createdByWallet;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", action=" + action +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", totalAmountInCents=" + totalAmountInCents +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", transactionType=" + transactionType +
                ", transactions=" + transactions +
                ", paymentMethods=" + paymentMethods +
                ", customPayload=" + customPayload +
                ", nsu='" + nsu + '\'' +
                ", createdByWallet=" + createdByWallet +
                ", createdByOwner=" + createdByOwner +
                ", orderDetailUuid='" + orderDetailUuid + '\'' +
                ", referenceOrderUuid='" + referenceOrderUuid + '\'' +
                '}';
    }
}
