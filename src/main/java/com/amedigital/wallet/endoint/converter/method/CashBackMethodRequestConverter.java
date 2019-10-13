package com.amedigital.wallet.endoint.converter.method;

import com.amedigital.wallet.constants.enuns.CashBackStatus;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.order.primary.PrimaryRequestConverter;
import com.amedigital.wallet.endoint.request.method.CashBackMethodRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.transaction.CashBackTransaction;

import java.time.ZonedDateTime;

public class CashBackMethodRequestConverter implements PrimaryRequestConverter<CashBackMethodRequest, CashBackTransaction> {


    @Override
    public CashBackTransaction from(CashBackMethodRequest cashBackMethodRequest, RequestContext context) {
        Wallet wallet = context.getTokenWallet()
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet no request."));

        var now = ZonedDateTime.now();

        return new CashBackTransaction.Builder()
                .setWalletId(wallet.getId().get())
                .setAmountInCents(cashBackMethodRequest.getAmountInCents())
                .setCashStatus(CashBackStatus.CREATED)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setCashCreatedAt(now)
                .setCashUpdatedAt(now)
                .build();
    }
}
