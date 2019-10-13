package com.amedigital.wallet.service.paymentmethod;

import com.amedigital.wallet.constants.enuns.CashBackStatus;
import com.amedigital.wallet.model.transaction.CashBackTransaction;
import com.amedigital.wallet.service.PaymentMethodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

import static com.amedigital.wallet.constants.enuns.TransactionStatus.*;

public class CashBackService implements PaymentMethodService<CashBackTransaction> {

    private static final Logger LOG = LoggerFactory.getLogger(CashBackService.class);

    @Autowired
    public CashBackService() {
    }

    @Override
    public Mono<CashBackTransaction> authorize(CashBackTransaction transaction) {
        LOG.info("Iniciando a autorização da transação de cashBack [{}]", transaction);

        ZonedDateTime now = ZonedDateTime.now();

        return Mono.just(transaction
                .copy()
                .setUpdatedAt(now)
                .setCashStatus(CashBackStatus.AUTHORIZED)
                .setCashUpdatedAt(now)
                .setStatus(AUTHORIZED)
                .build());
    }

    @Override
    public Mono<CashBackTransaction> capture(CashBackTransaction transaction) {
        LOG.info("Iniciando o captura da transação de cashBack [{}]", transaction);

        ZonedDateTime now = ZonedDateTime.now();

        return Mono.just(transaction
                .copy()
                .setStatus(CAPTURED)
                .setUpdatedAt(now)
                .setCashStatus(CashBackStatus.CAPTURED)
                .setCashUpdatedAt(now)
                .build());
    }

    @Override
    public Mono<CashBackTransaction> cancel(CashBackTransaction transaction) {
        return cancel(transaction, null);
    }

    @Override
    public Mono<CashBackTransaction> cancel(CashBackTransaction transaction, String reference) {
        LOG.info("Iniciando o cancelamento da transação de cashBack [{}]", transaction);

        ZonedDateTime now = ZonedDateTime.now();
        return Mono.just(transaction
                .copy()
                .setStatus(CANCELLED)
                .setUpdatedAt(now)
                .setCashStatus(CashBackStatus.CANCELLED)
                .setCashUpdatedAt(now)
                .build());
    }

    @Override
    public Mono<CashBackTransaction> findByCancellationReference(CashBackTransaction transaction) {
        return Mono.just(transaction);
    }

}
