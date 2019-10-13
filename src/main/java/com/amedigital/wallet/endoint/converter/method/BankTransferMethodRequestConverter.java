package com.amedigital.wallet.endoint.converter.method;

import com.amedigital.wallet.constants.enuns.TakeRateUnit;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.order.primary.PrimaryRequestConverter;
import com.amedigital.wallet.endoint.request.method.BankTransferMethodRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.transaction.BankTransferTransaction;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.amedigital.wallet.constants.enuns.BankTransferStatus.CREATED;

public class BankTransferMethodRequestConverter implements PrimaryRequestConverter<BankTransferMethodRequest, BankTransferTransaction>  {

	@Override
	public BankTransferTransaction from(BankTransferMethodRequest bankTransferRequest, RequestContext context) {
        
		Wallet wallet = context.getTokenWallet()
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet no request."));

        var now = ZonedDateTime.now();

        return new BankTransferTransaction.Builder()
                .setWalletId(wallet.getId().get())
                .setAmountInCents(bankTransferRequest.getAmountInCents())
                .setClientName(bankTransferRequest.getClientName())
                .setClientEmail(bankTransferRequest.getClientEmail())
                .setClientPhone(bankTransferRequest.getClientPhone())
                .setClientCPF(bankTransferRequest.getClientCPF())
                .setBank(bankTransferRequest.getBank())
                .setAgency(bankTransferRequest.getAgency())
                .setAccountNumber(bankTransferRequest.getAccountNumber())
                .setTaxApplied(bankTransferRequest.getTaxApplied())
                .setBankTransferStatus(CREATED)
                .setBankTransferType(bankTransferRequest.getBankTransferType())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setReleaseTime(0L)
                .setReleaseTimeUnit(ChronoUnit.DAYS)
                .setTakeRate(0L)
                .setReleaseTime(0L)
                .setReleaseDate(now)
                .setTakeRateUnit(TakeRateUnit.CURRENCY)
				.setGrossAmountInCents(bankTransferRequest.getAmountInCents())
				.setNetAmountInCents(bankTransferRequest.getAmountInCents())
				.setTakeRateAmountInCents(0L)

                .build();
	}
}
