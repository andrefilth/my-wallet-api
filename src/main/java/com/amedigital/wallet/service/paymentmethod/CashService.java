package com.amedigital.wallet.service.paymentmethod;

import com.amedigital.wallet.constants.enuns.CashStatus;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.service.PaymentMethodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Optional;

import static com.amedigital.wallet.constants.enuns.TransactionStatus.*;

@Service
public class CashService implements PaymentMethodService<CashTransaction> {

    private static final Logger LOG = LoggerFactory.getLogger(CashService.class);

    @Autowired
    public CashService() {

    }

    @Override
    public Mono<CashTransaction> authorize(CashTransaction transaction) {
        LOG.info("Iniciando a autorização da transação de cash [{}]", transaction);

        ZonedDateTime now = ZonedDateTime.now();

        return Mono.just(transaction
                .copy()
                .setUpdatedAt(now)
                .setCashStatus(CashStatus.AUTHORIZED)
                .setCashUpdatedAt(now)
                .setStatus(AUTHORIZED)
                .build());
    }

    @Override
    public Mono<CashTransaction> capture(CashTransaction transaction) {
        LOG.info("Iniciando o captura da transação de cash [{}]", transaction);

        ZonedDateTime now = ZonedDateTime.now();

        return Mono.just(transaction
                .copy()
                .setStatus(CAPTURED)
                .setUpdatedAt(now)
                .setCashStatus(CashStatus.CAPTURED)
                .setCashUpdatedAt(now)
                .build());
    }

    @Override
    public Mono<CashTransaction> cancel(CashTransaction transaction) {
        return cancel(transaction, null);
    }

    @Override
    public Mono<CashTransaction> cancel(CashTransaction transaction, String reference) {
        LOG.info("Iniciando o cancelamento da transação de cash [{}]", transaction);

        ZonedDateTime now = ZonedDateTime.now();
        return Mono.just(transaction
                .copy()
                .setStatus(CANCELLED)
                .setUpdatedAt(now)
                .setCashStatus(CashStatus.CANCELLED)
                .setCashUpdatedAt(now)
                .build());

    }

    @Override
    public Mono<CashTransaction> findByCancellationReference(CashTransaction transaction) {
        return Mono.just(transaction);
    }
}
