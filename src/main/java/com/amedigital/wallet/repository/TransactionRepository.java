package com.amedigital.wallet.repository;

import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import org.jdbi.v3.core.Handle;

import java.util.List;

public interface TransactionRepository {

    Transaction save(Handle handle, Long orderId, String orderUuid, Long actionId, Transaction transaction);

    CreditCardTransaction save(Handle handle, CreditCardTransaction creditCardTransaction);

    List<Transaction> save(Handle handle, Long orderId, String orderUuid, Long actionId, List<Transaction> transactions);

    List<Transaction> findByOrderId(Handle handle, Long orderId);

}