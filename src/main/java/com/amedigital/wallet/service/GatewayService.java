package com.amedigital.wallet.service;

import com.amedigital.wallet.service.atom.response.CancellationPresenter;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;

import com.amedigital.wallet.service.atom.request.PaymentParameter;
import com.amedigital.wallet.service.atom.response.OrderPresenter;

import com.amedigital.wallet.service.atom.request.CancellationParameter;

import reactor.core.publisher.Mono;

public interface GatewayService {

    Mono<OrderPresenter> authorization(PaymentParameter paymentParameter);

    Mono<CreditCardTransaction> capture(CreditCardTransaction transaction);

    Mono<CancellationPresenter> cancellation(CancellationParameter cancellationParameter, String paymentReference);

    Mono<CancellationPresenter> findByCancellationReference(String cancellationReference);

}
