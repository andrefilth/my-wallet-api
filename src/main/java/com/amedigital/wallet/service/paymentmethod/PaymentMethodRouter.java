package com.amedigital.wallet.service.paymentmethod;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;

import static com.amedigital.wallet.constants.enuns.PaymentMethod.*;

@Service
public class PaymentMethodRouter implements PaymentMethodService<Transaction> {

    private final HashMap<PaymentMethod, PaymentMethodService> gateways = new HashMap<>();

    @Autowired
    public PaymentMethodRouter(CreditCardService creditCardService,
                               CashService cashService,
                               BankTransferService bankTransferService,
                               CashBackService cashBackService) {

        gateways.put(CREDIT_CARD, creditCardService);
        gateways.put(CASH, cashService);
        gateways.put(BANK_TRANSFER, bankTransferService);
        gateways.put(CASH_BACK, cashBackService);
    }

    @Override
    public Mono<Transaction> authorize(Transaction transaction) {
        return gateways.get(transaction.getPaymentMethod()).authorize(transaction);
    }

    @Override
    public Mono<Transaction> capture(Transaction transaction) {
        return gateways.get(transaction.getPaymentMethod()).capture(transaction);
    }

    @Override
    public Mono<Transaction> cancel(Transaction transaction) {
        return cancel(transaction, null);
    }

    @Override
    public Mono<Transaction> cancel(Transaction transaction, String reference) {
        return  gateways.get(transaction.getPaymentMethod()).cancel(transaction, reference);
    }

    @Override
    public Mono<Transaction> findByCancellationReference(Transaction transaction) {
        return gateways.get(transaction.getPaymentMethod()).findByCancellationReference(transaction);
    }
}
