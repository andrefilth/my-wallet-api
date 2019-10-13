package com.amedigital.wallet.service.paymentmethod;

import com.amedigital.wallet.constants.enuns.CreditCardStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.converters.AuthorizationConverter;
import com.amedigital.wallet.converters.CancellationConverter;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.service.GatewayService;
import com.amedigital.wallet.service.PaymentMethodService;
import com.amedigital.wallet.service.WalletService;
import com.amedigital.wallet.service.atom.AtomService;
import com.amedigital.wallet.service.atom.request.CancellationParameter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
public class CreditCardService implements PaymentMethodService<CreditCardTransaction> {

    private final GatewayService gatewayService;
    private final WalletService walletService;

    @Autowired
    public CreditCardService(AtomService atomService, WalletService walletService) {
        this.gatewayService = atomService;
        this.walletService = walletService;
    }

    @Override
    public Mono<CreditCardTransaction> authorize(CreditCardTransaction transaction) {
        return walletService.findById(transaction.getWalletId())
                .flatMap(wallet -> gatewayService.authorization(AuthorizationConverter.toPaymentParameter(transaction, wallet.getUuid().get()))
                        .map(orderPresenter -> orderPresenter.getPayments()
                                .stream()
                                .map(pp -> AuthorizationConverter.toCreditCardTransaction(transaction, pp))
                                .findFirst()
                                .orElse(transaction.copy().setStatus(TransactionStatus.ERROR).setCreditCardStatus(CreditCardStatus.ERROR_TO_AUTHORIZE).build())
                        ))
                .flatMap(Mono::justOrEmpty)
                .onErrorResume(e -> Mono.just(transaction.copy().setStatus(TransactionStatus.DENIED).setCreditCardStatus(CreditCardStatus.ERROR_TO_AUTHORIZE).build()));
    }

    @Override
    public Mono<CreditCardTransaction> capture(CreditCardTransaction transaction) {
        return gatewayService.capture(transaction)
                .flatMap(Mono::just)
                .onErrorResume(e -> Mono.just(transaction.copy().setStatus(TransactionStatus.ERROR).setCreditCardStatus(CreditCardStatus.ERROR_TO_CAPTURE).build()));
    }

    @Override
    public Mono<CreditCardTransaction> cancel(CreditCardTransaction transaction) {
        return cancel(transaction, null);
    }

    @Override
    public Mono<CreditCardTransaction> cancel(CreditCardTransaction transaction, String reference) {
        var cancellationReference = Optional.ofNullable(reference)
                .filter(String::isBlank)
                .orElseGet(UUID.randomUUID()::toString);

        var cancellationParameter = CancellationConverter.toCancellationParameter(cancellationReference, transaction.getAmountInCents());

        return gatewayService.cancellation(cancellationParameter, transaction.getGatewayPaymentReference())
                .map(cancellationPresenter -> CancellationConverter.toCreditCardTransaction(transaction, cancellationPresenter))
                .onErrorResume(e -> Mono.just(transaction.copy().setStatus(TransactionStatus.ERROR).setCreditCardStatus(CreditCardStatus.CANCELLATION_ERROR).build()));
    }

    @Override
    public Mono<CreditCardTransaction> findByCancellationReference(CreditCardTransaction transaction) {
        if (StringUtils.isBlank(transaction.getGatewayCancellationReference())) {
            return Mono.error(new AmeInvalidInputException("wallet_validation", "Cancellation Reference não encontrada na transação para verificar o status de cancelamento."));
        }

        return gatewayService.findByCancellationReference(transaction.getGatewayCancellationReference())
                .map(cancellationPresenter -> CancellationConverter.toCreditCardTransaction(transaction, cancellationPresenter));
    }
}
