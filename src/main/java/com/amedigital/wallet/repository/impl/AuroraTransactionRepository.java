package com.amedigital.wallet.repository.impl;

import com.amedigital.wallet.constants.Constants;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.transaction.*;
import com.amedigital.wallet.repository.TransactionRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class AuroraTransactionRepository implements TransactionRepository {

    private final Jdbi jdbi;

    @Autowired
    public AuroraTransactionRepository(final Jdbi jdbi) {
        this.jdbi = jdbi;
    }


    @Override
    public List<Transaction> save(Handle handle, Long orderId, String orderUuid, Long actionId, List<Transaction> transactions) {
        return transactions.stream()
                .map(transaction -> save(handle, orderId, orderUuid, actionId, transaction))
                .collect(toList());
    }

    @Override
    public Transaction save(Handle handle, Long orderId, String orderUuid, Long actionId, Transaction transaction) {
        String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.transaction-insert");

        handle.execute("update tb_transaction set latest = false where uuid = ?", transaction.getUuid());

        return handle.createUpdate(sql)
                .bind("uuid", transaction.getUuid())
                .bind("order_id", orderId)
                .bind("order_uuid", orderUuid)
                .bind("action_id", actionId)
                .bind("wallet_id", transaction.getWalletId())
                .bind("status", transaction.getStatus())
                .bind("type", transaction.getType())
                .bind("payment_method", transaction.getPaymentMethod())

                .bind("amount_in_cents", transaction.getAmountInCents())

                .bind("take_rate", transaction.getTakeRate())
                .bind("take_rate_unit", transaction.getTakeRateUnit())
                .bind("take_rate_amount_in_cents", transaction.getTakeRateAmountInCents())

                .bind("release_date", transaction.getReleaseDate())

                .bind("peer_wallet_id", transaction.getPeerWalletId())
                .bind("peer_transaction_uuid", transaction.getPeerTransactionUuid())
                .bind("manager_wallet_id", Constants.DEFAULT_MANAGER_WALLET_ID)


                .bind("gross_amount_in_cents", transaction.getGrossAmountInCents())
                .bind("net_amount_in_cents", transaction.getNetAmountInCents())

                .bind("release_time", transaction.getReleaseTime())
                .bind("release_time_unit", transaction.getReleaseTimeUnit() != null ? transaction.getReleaseTimeUnit().toString() : null)

                .bind("latest", transaction.isLatest())
                .bind("created_at", transaction.getCreatedAt())
                .bind("updated_at", ZonedDateTime.now())

                .executeAndReturnGeneratedKeys("id")
                .mapTo(Long.class)
                .findFirst()
                .map(transactionId -> {

                    Transaction persistedTransaction = (Transaction) transaction.copy()
                            .setTransactionId(transactionId)
                            .build();

                    if (persistedTransaction instanceof CreditCardTransaction) {
                        return save(handle, (CreditCardTransaction) persistedTransaction);
                    } else if (persistedTransaction instanceof CashTransaction) {
                        return save(handle, (CashTransaction) persistedTransaction);
                    } else if (persistedTransaction instanceof BankTransferTransaction) {
                        return save(handle, (BankTransferTransaction) persistedTransaction);
                    } else if (persistedTransaction instanceof CashBackTransaction) {
                        return save(handle, (CashBackTransaction) persistedTransaction);
                    }

                    throw new AmeInternalException("No implementation found");

                }).orElseThrow(AmeInternalException::new);
    }


    @Override
    public CreditCardTransaction save(Handle handle, CreditCardTransaction creditCardTransaction) {
        String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.transaction-creditcard-insert");

        return handle.createUpdate(sql)
                .bind("transaction_id", creditCardTransaction.getTransactionId())
                .bind("credit_card_id", creditCardTransaction.getCreditCardId())
                .bind("credit_card_status", creditCardTransaction.getCreditCardStatus())
                .bind("gateway_order_reference", creditCardTransaction.getGatewayOrderReference())
                .bind("gateway_payment_reference", creditCardTransaction.getGatewayPaymentReference())
                .bind("acquirer", creditCardTransaction.getAcquirer())
                .bind("tid", creditCardTransaction.getTid())
                .bind("nsu", creditCardTransaction.getNsu())
                .bind("authorization_nsu", creditCardTransaction.getAuthorizationNsu())
                .bind("capture_nsu", creditCardTransaction.getCaptureNsu())
                .bind("cancel_nsu", creditCardTransaction.getCancelNsu())
                .bind("number_of_installments", creditCardTransaction.getNumberOfInstallments())
                .bind("installment_type", creditCardTransaction.getInstallmentType())
                .bind("authorization_code", creditCardTransaction.getAuthorizationCode())
                .bind("authorization_tid", creditCardTransaction.getAuthorizationTid())
                .bind("capture_tid", creditCardTransaction.getCaptureTid())
                .bind("cancel_tid", creditCardTransaction.getCancelTid())
                .bind("holder_name", creditCardTransaction.getHolderName())
                .bind("masked_number", creditCardTransaction.getMaskedNumber())
                .bind("brand", creditCardTransaction.getBrand())
                .bind("expiration_month", creditCardTransaction.getExpirationMonth())
                .bind("expiration_year", creditCardTransaction.getExpirationYear())
                .bind("currency", creditCardTransaction.getCurrency())
                .bind("authorization_date", creditCardTransaction.getAuthorizationDate())
                .bind("capture_date", creditCardTransaction.getCaptureDate())
                .bind("cancel_date", creditCardTransaction.getCancelDate())
                .bind("refund_date", creditCardTransaction.getRefundDate())
                .bind("gateway_cancellation_reference", creditCardTransaction.getGatewayCancellationReference())
                .bind("gateway_response_message", creditCardTransaction.getGatewayResponseMessage())
                .bind("gateway_response_code", creditCardTransaction.getGatewayResponseCode())
                .bind("created_at", creditCardTransaction.getCreatedAt())
                .bind("updated_at", ZonedDateTime.now())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(Long.class)
                .findFirst()
                .map(creditCardId -> creditCardTransaction.copy().setId(creditCardId).build())
                .orElseThrow(AmeInternalException::new);
    }

    private CashTransaction save(Handle handle, CashTransaction cashTransaction) {
        String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.transaction-cash-insert");

        return handle.createUpdate(sql)
                .bind("wallet_id", cashTransaction.getWalletId())
                .bind("transaction_id", cashTransaction.getTransactionId())
                .bind("amount_in_cents", cashTransaction.getAmountInCents())
                .bind("status", cashTransaction.getCashStatus())
                .bind("transaction_type", cashTransaction.getType())
                .bind("created_at", cashTransaction.getCreatedAt())
                .bind("updated_at", ZonedDateTime.now())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(Long.class)
                .findFirst()
                .map(cashId -> cashTransaction.copy().setId(cashId).build())
                .orElseThrow(AmeInternalException::new);
    }


    private CashBackTransaction save(Handle handle, CashBackTransaction cashTransaction) {
        String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.transaction-cash-back-insert");

        return handle.createUpdate(sql)
                .bind("wallet_id", cashTransaction.getWalletId())
                .bind("transaction_id", cashTransaction.getTransactionId())
                .bind("amount_in_cents", cashTransaction.getAmountInCents())
                .bind("status", cashTransaction.getCashStatus())
                .bind("transaction_type", cashTransaction.getType())
                .bind("created_at", cashTransaction.getCreatedAt())
                .bind("updated_at", ZonedDateTime.now())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(Long.class)
                .findFirst()
                .map(cashId -> cashTransaction.copy().setId(cashId).build())
                .orElseThrow(AmeInternalException::new);
    }

    private BankTransferTransaction save(Handle handle, BankTransferTransaction bankTransferTransaction) {
        String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.transaction-banktransfer-insert");

        return handle.createUpdate(sql)
                .bind("wallet_id", bankTransferTransaction.getWalletId())
                .bind("transaction_id", bankTransferTransaction.getTransactionId())
                .bind("amount_in_cents", bankTransferTransaction.getAmountInCents())
                .bind("bank_transfer_status", bankTransferTransaction.getBankTransferStatus())
                .bind("transaction_type", bankTransferTransaction.getType())
                .bind("created_at", bankTransferTransaction.getCreatedAt())
                .bind("updated_at", bankTransferTransaction.getUpdatedAt())
                .bind("client_name", bankTransferTransaction.getClientName())
                .bind("client_email", bankTransferTransaction.getClientEmail())
                .bind("client_phone", bankTransferTransaction.getClientPhone())
                .bind("client_cpf", bankTransferTransaction.getClientCPF())
                .bind("bank", bankTransferTransaction.getBank())
                .bind("agency", bankTransferTransaction.getAgency())
                .bind("account_number", bankTransferTransaction.getAccountNumber())
                .bind("tax_applied", bankTransferTransaction.getTaxApplied())
                .bind("bank_transfer_type", bankTransferTransaction.getBankTransferType())
                .bind("destination_agency", bankTransferTransaction.getDestinationAgency())
                .bind("destination_account", bankTransferTransaction.getDestinationAccount())
                .bind("destination_account_holder", bankTransferTransaction.getDestinationAccountHolder())
                .bind("destination_account_holder_document", bankTransferTransaction.getDestinationAccountHolderDocument())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(Long.class)
                .findFirst()
                .map(id -> bankTransferTransaction.copy().setId(id).build())
                .orElseThrow(AmeInternalException::new);
    }

    @Override
    public List<Transaction> findByOrderId(Handle handle, Long orderId) {
        if (handle.isClosed()) {
            return jdbi.withHandle(handle1 -> {
                String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.transaction-find-by-orderId");

                List<Transaction> transactions = handle1.createQuery(sql)
                        .bind("order_id", orderId)
                        .mapTo(Transaction.class)
                        .list();

                return transactions != null ? transactions : Collections.emptyList();
            });
        } else {
            String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.transaction-find-by-orderId");

            List<Transaction> transactions = handle.createQuery(sql)
                    .bind("order_id", orderId)
                    .mapTo(Transaction.class)
                    .list();

            return transactions != null ? transactions : Collections.emptyList();
        }

    }


}