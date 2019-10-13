package com.amedigital.wallet.endoint.converter.method;

import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.order.primary.PrimaryRequestConverter;
import com.amedigital.wallet.endoint.request.method.CashMethodRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.transaction.CashTransaction;

import java.time.ZonedDateTime;

import static com.amedigital.wallet.constants.enuns.CashStatus.CREATED;

public class CashMethodRequestConverter implements PrimaryRequestConverter<CashMethodRequest, CashTransaction> {

    @Override
    public CashTransaction from(CashMethodRequest cashMethodRequest, RequestContext context) {
        Wallet wallet = context.getTokenWallet()
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet no request."));

        var now = ZonedDateTime.now();

        return new CashTransaction.Builder()
                .setWalletId(wallet.getId().get())
                .setAmountInCents(cashMethodRequest.getAmountInCents())
                .setCashStatus(CREATED)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setCashCreatedAt(now)
                .setCashUpdatedAt(now)
                .build();
    }
}
