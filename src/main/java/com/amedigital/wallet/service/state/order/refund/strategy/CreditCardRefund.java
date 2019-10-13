package com.amedigital.wallet.service.state.order.refund.strategy;

import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.secondary.RefundOrder;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreditCardRefund implements RefundPaymentType<CreditCardTransaction> {

    private final PaymentMethodRouter paymentMethodRouter;
    private final OrderRepository repository;

    @Autowired
    public CreditCardRefund(PaymentMethodRouter paymentMethodRouter, OrderRepository repository) {
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
            return refundOrder.getTransactions()
                    .stream()
                    .filter(t -> PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod()) && TransactionStatus.CREATED.equals(t.getStatus()))
                    .findFirst()
                    .map(paymentMethodRouter::cancel)
                    .orElseThrow(() -> new AmeInvalidInputException("wallet_validation", "Não é possível realizar o estorno, pois não foi encontrada nenhuma transação com cartão de crédito."))
                    .map(gatewayTransaction -> createRefundOrderByGatewayResponse(gatewayTransaction, refundOrder))
                    .flatMap(repository::save)
                    .cast(SecondaryOrder.class);
        } else {
            return Mono.just(refundOrder);
        }
    }

    @Override
    public Mono<SecondaryOrder> checkRefundStatus(RefundOrder refundOrder) {
        if (OrderStatus.AUTHORIZED.equals(refundOrder.getStatus())) {
            return refundOrder.getTransactions()
                    .stream()
                    .filter(t -> PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod()))
                    .map(CreditCardTransaction.class::cast)
                    .filter(t -> CreditCardStatus.CANCELLATION_PENDING.equals(t.getCreditCardStatus()) || CreditCardStatus.CANCELLATION_ERROR.equals(t.getCreditCardStatus()))
                    .findFirst()
                    .map(t -> {
                        if (CreditCardStatus.CANCELLATION_PENDING.equals(t.getCreditCardStatus())) {
                            return paymentMethodRouter.findByCancellationReference(t);
                        }

                        return paymentMethodRouter.cancel(t, t.getGatewayCancellationReference());
                    })
                    .orElseThrow(() -> new AmeInvalidInputException("wallet_validation", "Nenhum transação de cartão encontrada com o status PENDENTE."))
                    .map(gatewayTransaction -> createRefundOrderByGatewayResponse(gatewayTransaction, refundOrder))
                    .flatMap(order -> {
                        if (!OrderStatus.AUTHORIZED.equals(order.getStatus())) {
                            return repository.save(order);
                        }

                        return Mono.just(order);
                    })
                    .cast(SecondaryOrder.class);
        } else {
            return Mono.error(new AmeInvalidInputException("wallet_validation", "Não é possivel alterar o status de um estorno quando não estiver AUTORIZADA."));
        }
    }

    private RefundOrder createRefundOrderByGatewayResponse(Transaction gatewayTransaction, RefundOrder refundOrder) {
        var now = ZonedDateTime.now();
        var updatedTransactions = new ArrayList<Transaction>();
        var refundedOrderCopy = refundOrder.copy();
        var gTransaction = (CreditCardTransaction) gatewayTransaction;

        var merchantCashTransaction = refundOrder.getTransactions()
                .stream()
                .filter(t -> PaymentMethod.CASH.equals(t.getPaymentMethod()))
                .map(merchantTransaction -> {
                    var merchantT = (CashTransaction) merchantTransaction;
                    var merchantTransactionCopy = merchantT.copy();

                    switch (gTransaction.getCreditCardStatus()) {
                        case CANCELLATION_PENDING:
                            merchantTransactionCopy.setStatus(TransactionStatus.PENDING)
                                    .setCashStatus(CashStatus.PENDING)
                                    .setCashUpdatedAt(now)
                                    .setUpdatedAt(now);
                            refundedOrderCopy.setStatus(OrderStatus.AUTHORIZED);
                            break;
                        case CANCELLATION_ERROR:
                            merchantTransactionCopy.setStatus(TransactionStatus.ERROR)
                                    .setCashStatus(CashStatus.ERROR)
                                    .setCashUpdatedAt(now)
                                    .setUpdatedAt(now);
                            refundedOrderCopy.setStatus(OrderStatus.AUTHORIZED);
                            break;
                        case CANCELLATION_REFUSED:
                            merchantTransactionCopy.setStatus(TransactionStatus.DENIED)
                                    .setCashStatus(CashStatus.DENIED)
                                    .setCashUpdatedAt(now)
                                    .setUpdatedAt(now);
                            refundedOrderCopy.setStatus(OrderStatus.DENIED);
                            break;
                        case CANCELLED:
                            merchantTransactionCopy.setStatus(TransactionStatus.CAPTURED)
                                    .setCashStatus(CashStatus.CAPTURED)
                                    .setCashUpdatedAt(now)
                                    .setUpdatedAt(now);
                            refundedOrderCopy.setStatus(OrderStatus.REFUNDED);
                            break;
                    }

                    return merchantTransactionCopy.build();
                })
                .findFirst()
                .orElseThrow(() -> new AmeInvalidInputException("wallet_validation", "Não foi possível realizar o estorno, pois não foi encontrada uma transação de cash."));

        updatedTransactions.add(merchantCashTransaction);

        if (TransactionStatus.CANCELLED.equals(gatewayTransaction.getStatus())) {
            updatedTransactions.add(gTransaction.copy().setStatus(TransactionStatus.CAPTURED).build());

        } else {
            updatedTransactions.add(gTransaction);

        }

        var action = new Action.Builder()
                .setType(ActionType.REFUND)
                .setParentId(refundOrder.getAction().getId())
                .setCreatedAt(now)
                .build();

        return refundedOrderCopy.setTransactions(updatedTransactions)
                .setUpdatedAt(now)
                .setAction(action)
                .setCreatedAt(refundOrder.getCreatedAt())
                .build();
    }

    private RefundOrder createRefundOrder(Order purchaseOrder, RefundOrder refundOrder) {
        var now = ZonedDateTime.now();

        var transaction = (CreditCardTransaction) purchaseOrder.getTransactions()
                .stream()
                .findFirst()
                .get();

        var refundedTransaction = new CreditCardTransaction.Builder() //crio a transação de extorno para cartão do tipo CREDIT.
                .setUuid(UUID.randomUUID().toString())
                .setWalletId(transaction.getWalletId())
                .setOrderUuid(refundOrder.getUuid())
                .setStatus(TransactionStatus.CREATED)
                .setType(TransactionType.CREDIT)
                .setAmountInCents(refundOrder.getTotalAmountInCents()) //??
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setPeerWalletId(transaction.getPeerWalletId())
                .setCreditCardStatus(CreditCardStatus.CREATED)
                .setNumberOfInstallments(transaction.getNumberOfInstallments())
                .setGatewayPaymentReference(transaction.getGatewayPaymentReference())
                .setGatewayOrderReference(transaction.getGatewayOrderReference())
                .setCreditCardId(transaction.getCreditCardId())
                .build();

        var merchantDebitCash = CashTransaction.builder() // crio a transação de cash para o merchat do tipo DEBIT no valor do extorno.
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
                .setPeerTransactionUuid(refundedTransaction.getUuid())
                .setAmountInCents(refundOrder.getTotalAmountInCents())
                .build();

        var action = new Action.Builder()
                .setCreatedAt(now)
                .setType(ActionType.CREATE)
                .build();

        List<Transaction> listTransactions = new ArrayList<>();
        listTransactions.add(refundedTransaction);
        listTransactions.add(merchantDebitCash);

        return refundOrder.copy()
                .setTransactions(listTransactions)
                .setAction(action)
                .build();
    }
}
