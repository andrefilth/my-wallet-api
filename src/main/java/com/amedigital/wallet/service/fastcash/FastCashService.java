package com.amedigital.wallet.service.fastcash;

import com.amedigital.wallet.service.fastcash.request.CashInRequest;
import com.amedigital.wallet.service.fastcash.request.CashOutRequest;
import com.amedigital.wallet.service.fastcash.response.CashInResponse;
import com.amedigital.wallet.service.fastcash.response.CashOutResponse;

import reactor.core.publisher.Mono;

public interface FastCashService {
	
	public Mono<CashOutResponse> requestCashOutToBankAccount(CashOutRequest payoutRequest);
	
	public Mono<CashInResponse> requestCashInFromBankAccount(CashInRequest cashInRequest);

}
