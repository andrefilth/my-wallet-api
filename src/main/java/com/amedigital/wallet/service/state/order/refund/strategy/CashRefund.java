package com.amedigital.wallet.service.state.order.refund.strategy;

import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.secondary.RefundOrder;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CashRefund implements RefundPaymentType<CashTransaction> {

    private final PaymentMethodRouter paymentMethodRouter;
    private final OrderRepository repository;

    @Autowired
    public CashRefund(PaymentMethodRouter paymentMethodRouter, OrderRepository repository) {
        this.paymentMethodRouter = paymentMethodRouter;
        this.repository = repository;
    }

    @Override
    public Mono<SecondaryOrder> create(Order purchaseOrder, RefundOrder refundOrder) {
        if (OrderStatus.CAPTURED.equals(purchaseOrder.getStatus())) {
            return repository.findByOrderReference(refundOrder.getReferenceOrderUuid())
                    .filter(t -> OrderType.REFUND.equals(t.getType()) &&
                            (OrderStatus.AUTHORIZED.equals(t.getStatus()) ||
                                    OrderStatus.REFUNDED.equals(t.getStatus()))
                    )
                    .reduce(0L, (a, acc) -> a + acc.getTotalAmountInCents())
                    .map(totalRefunded -> purchaseOrder.getTotalAmountInCents() - totalRefunded)
                    .flatMap(availableAmountToRefunded -> {
                        if (availableAmountToRefunded < refundOrder.getTotalAmountInCents()) {
                            return Mono.error(new AmeInvalidInputException("wallet_validation",
                                    "O valor solicitado para o extorno é superior ao valor da compra."));
                        }

                        return Mono.just(availableAmountToRefunded);
                    })
                    .map($ -> createRefundOrder(purchaseOrder, refundOrder))
                    .flatMap(repository::save)
                    .cast(RefundOrder.class)
                    .flatMap(this::refund);

        } else {
            return Mono.error(new AmeInvalidInputException("wallet_validation", "Você não pode extornar uma ordem não capturada."));
        }
    }

    @Override
    public Mono<SecondaryOrder> refund(RefundOrder refundOrder) {
        if (OrderStatus.CREATED.equals(refundOrder.getStatus())) {
            return Flux.fromStream(refundOrder.getTransactions()
                    .stream())
                    .filter(t -> PaymentMethod.CASH.equals(t.getPaymentMethod())
                            && TransactionStatus.CREATED.equals(t.getStatus()))
                    .flatMap(paymentMethodRouter::cancel)
                    .collectList()
                    .map(transactionResponse -> createRefundOrderByResponse(transactionResponse, refundOrder))
                    .flatMap(repository::save)
                    .cast(SecondaryOrder.class);
        } else {
            return Mono.just(refundOrder);
        }
    }

    @Override
    public Mono<SecondaryOrder> checkRefundStatus(RefundOrder refundOrder) {
        return Mono.just(refundOrder);
    }

    private RefundOrder createRefundOrder(Order purchaseOrder, RefundOrder refundOrder) {
        var now = ZonedDateTime.now();

        var transaction = purchaseOrder.getTransactions()
                .stream()
                .findFirst()
                .map(CashTransaction.class::cast)
                .get();

        var userRefundedTransaction = new CashTransaction.Builder()
                .setUuid(UUID.randomUUID().toString())
                .setWalletId(transaction.getWalletId())
                .setOrderUuid(refundOrder.getUuid())
                .setStatus(TransactionStatus.CREATED)
                .setType(TransactionType.CREDIT)
                .setAmountInCents(refundOrder.getTotalAmountInCents())
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setPeerWalletId(transaction.getPeerWalletId())
                .setCashStatus(CashStatus.CREATED)
                .setAmountInCents(refundOrder.getTotalAmountInCents())
                .setCashCreatedAt(now)
                .setCashUpdatedAt(now)
                .build();

        var merchantDebitCash = CashTransaction.builder()
                .setCashStatus(CashStatus.CREATED)
                .setCashUpdatedAt(now)
                .setCashCreatedAt(now)
                .setUuid(UUID.randomUUID().toString())
                .setWalletId(transaction.getPeerWalletId())
                .setOrderUuid(refundOrder.getUuid())
                .setStatus(TransactionStatus.CREATED)
                .setType(TransactionType.DEBIT)
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setPeerWalletId(transaction.getWalletId())
                .setPeerTransactionUuid(userRefundedTransaction.getUuid())
                .setAmountInCents(refundOrder.getTotalAmountInCents())
                .build();

        var action = new Action.Builder()
                .setCreatedAt(now)
                .setType(ActionType.CREATE)
                .build();

        List<Transaction> listTransactions = new ArrayList<>();
        listTransactions.add(userRefundedTransaction);
        listTransactions.add(merchantDebitCash);

        return refundOrder.copy()
                .setTransactions(listTransactions)
                .setAction(action)
                .build();
    }

    private RefundOrder createRefundOrderByResponse(List<Transaction> transactions, RefundOrder refundOrder) {
        var now = ZonedDateTime.now();

        var updatedTransactions = transactions.stream()
                .filter(t -> PaymentMethod.CASH.equals(t.getPaymentMethod()))
                .map(CashTransaction.class::cast)
                .map(t -> t.copy()
                        .setStatus(TransactionStatus.CAPTURED)
                        .setCashStatus(CashStatus.CAPTURED)
                        .build())
                .map(Transaction.class::cast)
                .collect(Collectors.toList());

        var action = new Action.Builder()
                .setType(ActionType.REFUND)
                .setParentId(refundOrder.getAction().getId())
                .setCreatedAt(now)
                .build();

        return refundOrder.copy()
                .setTransactions(updatedTransactions)
                .setUpdatedAt(now)
                .setAction(action)
                .setCreatedAt(refundOrder.getCreatedAt())
                .setStatus(OrderStatus.REFUNDED)
                .build();
    }
}
