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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MultipleRefund implements RefundPaymentType<CreditCardRefund> {

    private final OrderRepository repository;
    private final PaymentMethodRouter paymentMethodRouter;

    @Autowired
    public MultipleRefund(OrderRepository repository, PaymentMethodRouter paymentMethodRouter) {
        this.repository = repository;
        this.paymentMethodRouter = paymentMethodRouter;
    }

    @Override
    public Mono<SecondaryOrder> create(Order purchaseOrder, RefundOrder refundOrder) {
        if (OrderStatus.CAPTURED.equals(purchaseOrder.getStatus())) {
            return repository.findByOrderReference(refundOrder.getReferenceOrderUuid())
                    .collectList()
                    .flatMap(listAll -> Flux.fromStream(listAll.stream())
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
                            .map($ -> createTransactions(purchaseOrder, listAll, refundOrder))
                            .flatMap(repository::save)
                            .cast(RefundOrder.class)
                            .flatMap(this::refund));
        } else {
            return Mono.error(new AmeInvalidInputException("wallet_validation", "Você não pode extornar uma ordem não capturada."));
        }
    }

    @Override
    public Mono<SecondaryOrder> refund(RefundOrder refundOrder) {
        if (OrderStatus.CREATED.equals(refundOrder.getStatus())) {
            var creditCardTransaction = refundOrder.getTransactions()
                    .stream()
                    .filter(t -> PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod())
                            && TransactionStatus.CREATED.equals(t.getStatus()))
                    .findFirst()
                    .map(CreditCardTransaction.class::cast);

            var cashTransaction = refundOrder.getTransactions()
                    .stream()
                    .filter(t -> PaymentMethod.CASH.equals(t.getPaymentMethod()) &&
                            TransactionStatus.CREATED.equals(t.getStatus()) &&
                            TransactionType.CREDIT.equals(t.getType()))
                    .findFirst()
                    .map(CashTransaction.class::cast);

            List<Transaction> transactionList = new ArrayList<>();
            creditCardTransaction.ifPresent(transactionList::add);
            cashTransaction.ifPresent(transactionList::add);

            return Flux.fromStream(transactionList.stream())
                    .flatMap(paymentMethodRouter::cancel)
                    .collectList()
                    .map(transactionsResponse -> checkRefundTransactions(transactionsResponse, refundOrder))
                    .flatMap(repository::save)
                    .cast(SecondaryOrder.class);
        } else {
            return Mono.just(refundOrder);
        }
    }

    @Override
    public Mono<SecondaryOrder> checkRefundStatus(RefundOrder refundOrder) {
        if (OrderStatus.AUTHORIZED.equals(refundOrder.getStatus())) {
            var creditCardTransaction = refundOrder.getTransactions()
                    .stream()
                    .filter(t -> PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod())
                            && TransactionStatus.PENDING.equals(t.getStatus()))
                    .findFirst()
                    .map(CreditCardTransaction.class::cast);

            List<Transaction> transactionList = new ArrayList<>();
            creditCardTransaction.ifPresent(transactionList::add);

            return Flux.fromStream(transactionList.stream())
                    .flatMap(t -> {
                        var cardTransaction = (CreditCardTransaction) t;

                        if (CreditCardStatus.CANCELLATION_PENDING.equals(cardTransaction.getCreditCardStatus())) {
                            return paymentMethodRouter.findByCancellationReference(cardTransaction);
                        }

                        return paymentMethodRouter.cancel(cardTransaction, cardTransaction.getGatewayCancellationReference());
                    })
                    .collectList()
                    .map(transactionsResponse -> checkRefundTransactions(transactionsResponse, refundOrder))
                    .flatMap(order -> {
                        if (!OrderStatus.AUTHORIZED.equals(order.getStatus())) {
                            return repository.save(order);
                        }

                        return Mono.just(order);
                    })
                    .map(RefundOrder.class::cast)
                    .map(order -> mergeOrders(order, refundOrder));
        } else {
            return Mono.error(new AmeInvalidInputException("wallet_validation", "Não é possivel alterar o status de um estorno quando não estiver PENDENTE."));
        }
    }

    private SecondaryOrder mergeOrders(RefundOrder updatedRefundOrder, RefundOrder oldRefundOrder) {
        List<Transaction> cashTransactions = new ArrayList<>();
        List<Transaction> creditCardTransactions = updatedRefundOrder.getTransactions();
        List<Transaction> transactions;

        var optionalCreditCashTransaction = oldRefundOrder.getTransactions()
                .stream()
                .filter(transaction -> PaymentMethod.CASH.equals(transaction.getPaymentMethod()) &&
                        TransactionType.CREDIT.equals(transaction.getType()) &&
                        TransactionStatus.CAPTURED.equals(transaction.getStatus()))
                .findFirst();

        if (optionalCreditCashTransaction.isPresent()) {
            var creditCashTransaction = optionalCreditCashTransaction.map(CashTransaction.class::cast).get();

            var debitCashTransaction = oldRefundOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH.equals(transaction.getPaymentMethod()) &&
                            creditCashTransaction.getUuid().equals(transaction.getPeerTransactionUuid()) &&
                            TransactionStatus.CAPTURED.equals(transaction.getStatus()))
                    .findFirst()
                    .map(CashTransaction.class::cast)
                    .get();

            cashTransactions.add(creditCashTransaction);
            cashTransactions.add(debitCashTransaction);

            transactions = Stream.of(creditCardTransactions, cashTransactions)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            return updatedRefundOrder.copy()
                    .setTransactions(transactions)
                    .build();
        } else {
            return updatedRefundOrder;
        }
    }

    private RefundOrder createTransactions(Order purchaseOrder, List<SecondaryOrder> listOrders, RefundOrder refundOrder) {
        var refundedOrders = listOrders.stream()
                .filter(o -> OrderType.REFUND.equals(o.getType()) &&
                        (OrderStatus.AUTHORIZED.equals(o.getStatus()) ||
                                OrderStatus.REFUNDED.equals(o.getStatus())))
                .collect(Collectors.toList());

        var totalCashRefunded = refundedOrders.stream()
                .flatMap(o -> o.getTransactions().stream())
                .filter(t -> PaymentMethod.CASH.equals(t.getPaymentMethod()) &&
                        TransactionStatus.CAPTURED.equals(t.getStatus()) &&
                        TransactionType.CREDIT.equals(t.getType()))
                .mapToLong(Transaction::getAmountInCents)
                .sum();

        var totalCreditCardRefunded = refundedOrders.stream()
                .flatMap(o -> o.getTransactions().stream())
                .filter(t -> PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod()) &&
                        (TransactionStatus.CANCELLED.equals(t.getStatus()) ||
                                TransactionStatus.PENDING.equals(t.getStatus())) &&
                        TransactionType.CREDIT.equals(t.getType()))
                .mapToLong(Transaction::getAmountInCents)
                .sum();

        var purchaseCashTransaction = (CashTransaction) purchaseOrder.getTransactions()
                .stream()
                .filter(t -> PaymentMethod.CASH.equals(t.getPaymentMethod()))
                .findFirst()
                .get();

        var purchaseCreditCardTransaction = (CreditCardTransaction) purchaseOrder.getTransactions()
                .stream()
                .filter(t -> PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod()))
                .findFirst()
                .get();

        var cashAvailableRefund = purchaseCashTransaction.getAmountInCents() - totalCashRefunded;
        var creditCardAvailableRefund = purchaseCreditCardTransaction.getAmountInCents() - totalCreditCardRefunded;

        List<Transaction> listTransactions;

        if (refundOrder.getTotalAmountInCents() <= cashAvailableRefund) {
            // estornar tudo no cash.
            listTransactions = createCashRefund(purchaseCashTransaction, refundOrder.getTotalAmountInCents(), refundOrder.getUuid());

        } else {
            if (cashAvailableRefund > 0) {
                //cartao + saldo
                var listCashRefund = createCashRefund(purchaseCashTransaction, cashAvailableRefund, refundOrder.getUuid());
                List<Transaction> listCardRefund;
                var creditCardValueRefund = refundOrder.getTotalAmountInCents() - cashAvailableRefund;

                if (creditCardAvailableRefund < creditCardValueRefund) {
                    throw new AmeInvalidInputException("wallet_validation", "Não é possível realizar o estorno no cartão de credito.");
                }

                listCardRefund = createCreditCardRefund(purchaseCreditCardTransaction, creditCardValueRefund, refundOrder.getUuid());

                listTransactions = Stream.of(listCashRefund, listCardRefund)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
            } else {
                if (refundOrder.getTotalAmountInCents() > creditCardAvailableRefund) {
                    throw new AmeInvalidInputException("wallet_validation", "Não é possível realizar o estorno no cartão de credito.");
                }

                //estorna tudo no cartao.
                listTransactions = createCreditCardRefund(purchaseCreditCardTransaction, refundOrder.getTotalAmountInCents(), refundOrder.getUuid());
            }
        }

        var action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.CREATE)
                .build();

        return refundOrder.copy()
                .setTransactions(listTransactions)
                .setAction(action)
                .build();
    }

    private List<Transaction> createCashRefund(CashTransaction purchaseCashTransaction, Long amountToRefund, String orderUuid) {
        var now = ZonedDateTime.now();

        var userRefundedTransaction = new CashTransaction.Builder()
                .setUuid(UUID.randomUUID().toString())
                .setWalletId(purchaseCashTransaction.getWalletId())
                .setOrderUuid(orderUuid)
                .setStatus(TransactionStatus.CREATED)
                .setType(TransactionType.CREDIT)
                .setAmountInCents(amountToRefund)
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setPeerWalletId(purchaseCashTransaction.getPeerWalletId())
                .setCashStatus(CashStatus.CREATED)
                .setCashCreatedAt(now)
                .setCashUpdatedAt(now)
                .build();

        var merchantDebitCash = CashTransaction.builder()
                .setCashStatus(CashStatus.CREATED)
                .setCashUpdatedAt(now)
                .setCashCreatedAt(now)
                .setUuid(UUID.randomUUID().toString())
                .setWalletId(purchaseCashTransaction.getPeerWalletId())
                .setOrderUuid(orderUuid)
                .setStatus(TransactionStatus.CREATED)
                .setType(TransactionType.DEBIT)
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setPeerWalletId(purchaseCashTransaction.getWalletId())
                .setPeerTransactionUuid(userRefundedTransaction.getUuid())
                .setAmountInCents(amountToRefund)
                .build();

        List<Transaction> listTransactions = new ArrayList<>();
        listTransactions.add(userRefundedTransaction);
        listTransactions.add(merchantDebitCash);

        return listTransactions;
    }

    private List<Transaction> createCreditCardRefund(CreditCardTransaction purchaseCreditCardTransaction, Long amountToRefund, String orderUuid) {
        var now = ZonedDateTime.now();

        var refundedTransaction = new CreditCardTransaction.Builder() //crio a transação de extorno para cartão do tipo CREDIT.
                .setUuid(UUID.randomUUID().toString())
                .setWalletId(purchaseCreditCardTransaction.getWalletId())
                .setOrderUuid(orderUuid)
                .setStatus(TransactionStatus.CREATED)
                .setType(TransactionType.CREDIT)
                .setAmountInCents(amountToRefund) //??
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setPeerWalletId(purchaseCreditCardTransaction.getPeerWalletId())
                .setCreditCardStatus(CreditCardStatus.CREATED)
                .setNumberOfInstallments(purchaseCreditCardTransaction.getNumberOfInstallments())
                .setGatewayPaymentReference(purchaseCreditCardTransaction.getGatewayPaymentReference())
                .setGatewayOrderReference(purchaseCreditCardTransaction.getGatewayOrderReference())
                .setCreditCardId(purchaseCreditCardTransaction.getCreditCardId())
                .build();

        var merchantDebitCash = CashTransaction.builder() // crio a transação de cash para o merchat do tipo DEBIT no valor do extorno.
                .setCashStatus(CashStatus.CREATED)
                .setCashUpdatedAt(now)
                .setCashCreatedAt(now)
                .setUuid(UUID.randomUUID().toString())
                .setWalletId(purchaseCreditCardTransaction.getPeerWalletId())
                .setOrderUuid(purchaseCreditCardTransaction.getUuid())
                .setStatus(TransactionStatus.CREATED)
                .setType(TransactionType.DEBIT)
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setPeerWalletId(purchaseCreditCardTransaction.getWalletId())
                .setPeerTransactionUuid(refundedTransaction.getUuid())
                .setAmountInCents(amountToRefund)
                .build();

        List<Transaction> listTransactions = new ArrayList<>();
        listTransactions.add(refundedTransaction);
        listTransactions.add(merchantDebitCash);

        return listTransactions;
    }

    private RefundOrder checkRefundTransactions(List<Transaction> transactionsResponse, RefundOrder refundOrder) {
        List<Transaction> cashTransactions = new ArrayList<>();
        List<Transaction> cardTransaction = new ArrayList<>();
        List<Transaction> transactions;

        var now = ZonedDateTime.now();

        var optionalCreditCardTransaction = transactionsResponse.stream()
                .filter(transaction -> PaymentMethod.CREDIT_CARD.equals(transaction.getPaymentMethod()))
                .map(CreditCardTransaction.class::cast)
                .findFirst();

        var optionalCashTransaction = transactionsResponse.stream()
                .filter(transaction -> PaymentMethod.CASH.equals(transaction.getPaymentMethod()))
                .map(CashTransaction.class::cast)
                .findFirst();

        if (optionalCreditCardTransaction.isPresent()) {
            var creditCardTransaction = optionalCreditCardTransaction.get();

            var merchantTransaction = refundOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH.equals(transaction.getPaymentMethod()) &&
                            creditCardTransaction.getUuid().equals(transaction.getPeerTransactionUuid()))
                    .map(CashTransaction.class::cast)
                    .findFirst()
                    .get();

            cardTransaction = updateRefundTransactionByGatewayResponse(creditCardTransaction, merchantTransaction);
        }

        if (optionalCashTransaction.isPresent()) {
            var creditCashTransaction = optionalCashTransaction.get();

            var debitCashTransaction = refundOrder.getTransactions()
                    .stream()
                    .filter(transaction -> PaymentMethod.CASH.equals(transaction.getPaymentMethod()) &&
                            creditCashTransaction.getUuid().equals(transaction.getPeerTransactionUuid()))
                    .map(CashTransaction.class::cast)
                    .findFirst()
                    .get();

            cashTransactions = updateRefundedCashTransactions(debitCashTransaction, creditCashTransaction);
        }

        transactions = Stream.of(cardTransaction, cashTransactions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        var isRefunded = transactions.stream()
                .allMatch(transaction -> TransactionStatus.CAPTURED.equals(transaction.getStatus()));

        var action = new Action.Builder()
                .setType(ActionType.REFUND)
                .setParentId(refundOrder.getAction().getId())
                .setCreatedAt(now)
                .build();

        return refundOrder.copy()
                .setTransactions(transactions)
                .setUpdatedAt(now)
                .setAction(action)
                .setStatus(isRefunded ? OrderStatus.REFUNDED : OrderStatus.AUTHORIZED)
                .setCreatedAt(refundOrder.getCreatedAt())
                .build();
    }

    private List<Transaction> updateRefundedCashTransactions(CashTransaction debitCashTransaction, CashTransaction creditCashTransaction) {
        List<Transaction> transactions = new ArrayList<>();
        var now = ZonedDateTime.now();
        var updatedDebitCashTransaction = debitCashTransaction;
        var updatedCreditCashTransaction = creditCashTransaction;

        if (TransactionStatus.CANCELLED.equals(creditCashTransaction.getStatus())) {
            updatedCreditCashTransaction = creditCashTransaction.copy()
                    .setStatus(TransactionStatus.CAPTURED)
                    .setCashUpdatedAt(now)
                    .setUpdatedAt(now)
                    .setCashStatus(CashStatus.CAPTURED)
                    .build();

            updatedDebitCashTransaction = debitCashTransaction.copy()
                    .setStatus(TransactionStatus.CAPTURED)
                    .setCashUpdatedAt(now)
                    .setUpdatedAt(now)
                    .setCashStatus(CashStatus.CAPTURED)
                    .build();
        }

        transactions.add(updatedCreditCashTransaction);
        transactions.add(updatedDebitCashTransaction);

        return transactions;
    }

    private List<Transaction> updateRefundTransactionByGatewayResponse(CreditCardTransaction gatewayTransaction, CashTransaction cashMerchantCreditCard) {
        var now = ZonedDateTime.now();
        List<Transaction> updatedTransactions = new ArrayList<>();
        var merchantTransactionCopy = cashMerchantCreditCard.copy();

        switch (gatewayTransaction.getCreditCardStatus()) {
            case CANCELLATION_PENDING:
                merchantTransactionCopy.setStatus(TransactionStatus.PENDING)
                        .setCashStatus(CashStatus.PENDING)
                        .setCashUpdatedAt(now)
                        .setUpdatedAt(now);
                break;
            case CANCELLATION_ERROR:
                merchantTransactionCopy.setStatus(TransactionStatus.ERROR)
                        .setCashStatus(CashStatus.ERROR)
                        .setCashUpdatedAt(now)
                        .setUpdatedAt(now);
                break;
            case CANCELLATION_REFUSED:
                merchantTransactionCopy.setStatus(TransactionStatus.DENIED)
                        .setCashStatus(CashStatus.DENIED)
                        .setCashUpdatedAt(now)
                        .setUpdatedAt(now);
                break;
            case CANCELLED:
                merchantTransactionCopy.setStatus(TransactionStatus.CAPTURED)
                        .setCashStatus(CashStatus.CAPTURED)
                        .setCashUpdatedAt(now)
                        .setUpdatedAt(now);
                break;
        }

        updatedTransactions.add(merchantTransactionCopy.build());

        if (TransactionStatus.CANCELLED.equals(gatewayTransaction.getStatus())) {
            updatedTransactions.add(gatewayTransaction.copy()
                    .setStatus(TransactionStatus.CAPTURED)
                    .build());
        } else {
            updatedTransactions.add(gatewayTransaction);
        }

        return updatedTransactions;
    }
}
