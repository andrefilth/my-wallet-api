package com.amedigital.wallet.service.paymentmethod.banktransfer;

import static com.amedigital.wallet.constants.enuns.TransactionStatus.CANCELLED;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amedigital.wallet.constants.enuns.BankTransferStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.transaction.BankTransferTransaction;
import com.amedigital.wallet.service.PaymentMethodService;
import com.amedigital.wallet.service.fastcash.FastCashService;
import com.amedigital.wallet.service.fastcash.converters.CashOutRequestConverter;

import reactor.core.publisher.Mono;

@Component
public class BankTransferCashOutService implements PaymentMethodService<BankTransferTransaction> {

    private static final Logger LOG = LoggerFactory.getLogger(BankTransferCashOutService.class);
    
    private final FastCashService fastCashService;

    @Autowired
    public BankTransferCashOutService(FastCashService fastCashService) {
        this.fastCashService = fastCashService;
    }

    @Override
    public Mono<BankTransferTransaction> authorize(BankTransferTransaction transaction) {

        return fastCashService.requestCashOutToBankAccount(CashOutRequestConverter.fromTransaction(transaction))
                .flatMap(cashOutResponse -> Mono.just(CashOutRequestConverter.toTransaction(transaction)))
                .doOnSuccess(bankTransferTransaction -> LOG.info("Resposta de autorização da Transferencia bancaria [{}]", bankTransferTransaction))
                .onErrorResume(e -> {
                    LOG.error("Ocorreu o seguinte erro ao autorizar a transferencia [{}], [{}], [{}]", transaction, e.getMessage(), e.getStackTrace());
                    return Mono.just(transaction.copy().setStatus(TransactionStatus.DENIED).setBankTransferStatus(BankTransferStatus.DENIED).build());
                });
    }

    @Override
    public Mono<BankTransferTransaction> capture(BankTransferTransaction transaction) {

        return Mono.just(transaction
                .copy()
                .setStatus(TransactionStatus.CAPTURED)
                .setUpdatedAt(ZonedDateTime.now())
                .setBankTransferStatus(BankTransferStatus.CAPTURED)
                .build());
    }

    @Override
    public Mono<BankTransferTransaction> cancel(BankTransferTransaction transaction) {
    	
        return Mono.just(transaction
                .copy()
                .setStatus(CANCELLED)
                .setUpdatedAt(ZonedDateTime.now())
                .setBankTransferStatus(BankTransferStatus.CANCELLED)
                .build());
    }

    @Override
    public Mono<BankTransferTransaction> cancel(BankTransferTransaction transaction, String reference) {
        return Mono.error(new AmeInvalidInputException("wallet_validation", "Cancelamento nao disponivel para cashout"));
    }

    @Override
    public Mono<BankTransferTransaction> findByCancellationReference(BankTransferTransaction transaction) {
        return Mono.error(new AmeInvalidInputException("wallet_validation", "findByCancellationReference nao disponivel para cashout"));
    }

}
