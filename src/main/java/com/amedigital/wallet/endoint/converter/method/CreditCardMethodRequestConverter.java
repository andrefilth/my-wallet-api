package com.amedigital.wallet.endoint.converter.method;

import com.amedigital.wallet.constants.enuns.CreditCardStatus;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.order.primary.PrimaryRequestConverter;
import com.amedigital.wallet.endoint.request.method.CreditCardMethodRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;

import java.time.ZonedDateTime;
import java.util.UUID;

public class CreditCardMethodRequestConverter implements PrimaryRequestConverter<CreditCardMethodRequest, CreditCardTransaction> {


    @Override
    public CreditCardTransaction from(CreditCardMethodRequest creditCardRequest, RequestContext context) {
        Wallet wallet = context.getTokenWallet()
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet no request."));

        ZonedDateTime now = ZonedDateTime.now();

        return new CreditCardTransaction.Builder()
                .setWalletId(wallet.getId().get())
                .setCreditCardStatus(CreditCardStatus.CREATED)
                .setAmountInCents(creditCardRequest.getAmountInCents())
                .setCreditCardId(creditCardRequest.getCreditCardId())
                .setCvv(creditCardRequest.getCvv())
                .setNumberOfInstallments(creditCardRequest.getNumberOfInstallments())
                .setGatewayPaymentReference(UUID.randomUUID().toString())
                .setGatewayOrderReference(context.getPrimaryOrderUuid().orElse(UUID.randomUUID().toString()))
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();
    }
}