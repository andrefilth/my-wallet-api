package com.amedigital.wallet.service.paymentmethod;

import static com.amedigital.wallet.model.transaction.BankTransferTransaction.BankTransferType.BANK_CASH_IN;
import static com.amedigital.wallet.model.transaction.BankTransferTransaction.BankTransferType.BANK_CASH_OUT;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.model.transaction.BankTransferTransaction;
import com.amedigital.wallet.model.transaction.BankTransferTransaction.BankTransferType;
import com.amedigital.wallet.service.PaymentMethodService;
import com.amedigital.wallet.service.paymentmethod.banktransfer.BankTransferCashInService;
import com.amedigital.wallet.service.paymentmethod.banktransfer.BankTransferCashOutService;

import reactor.core.publisher.Mono;

@Service
public class BankTransferService implements PaymentMethodService<BankTransferTransaction> {

    private final Map<BankTransferType, PaymentMethodService<BankTransferTransaction>> bankTransferStrategy = new HashMap<>();
    
    @Autowired
    public BankTransferService(BankTransferCashOutService bankTransferCashOutService, BankTransferCashInService bankTransferCashInService) {
        bankTransferStrategy.put(BANK_CASH_OUT, bankTransferCashOutService);
        bankTransferStrategy.put(BANK_CASH_IN, bankTransferCashInService);
    }

    @Override
    public Mono<BankTransferTransaction> authorize(BankTransferTransaction transaction) {
    	return bankTransferStrategy.get(transaction.getBankTransferType()).authorize(transaction);
    }

    @Override
    public Mono<BankTransferTransaction> capture(BankTransferTransaction transaction) {
    	return bankTransferStrategy.get(transaction.getBankTransferType()).capture(transaction);
    }

    @Override
    public Mono<BankTransferTransaction> cancel(BankTransferTransaction transaction) {
    	return bankTransferStrategy.get(transaction.getBankTransferType()).cancel(transaction);
    }

    @Override
    public Mono<BankTransferTransaction> cancel(BankTransferTransaction transaction, String reference) {
    	return bankTransferStrategy.get(transaction.getBankTransferType()).cancel(transaction, reference);
    }

    @Override
    public Mono<BankTransferTransaction> findByCancellationReference(BankTransferTransaction transaction) {
    	return bankTransferStrategy.get(transaction.getBankTransferType()).findByCancellationReference(transaction);
    }

}
