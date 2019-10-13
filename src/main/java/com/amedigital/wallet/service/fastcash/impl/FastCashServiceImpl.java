package com.amedigital.wallet.service.fastcash.impl;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.amedigital.wallet.config.FastCashConfig;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.service.fastcash.FastCashService;
import com.amedigital.wallet.service.fastcash.request.CashInRequest;
import com.amedigital.wallet.service.fastcash.request.CashOutRequest;
import com.amedigital.wallet.service.fastcash.response.CashInResponse;
import com.amedigital.wallet.service.fastcash.response.CashOutResponse;
import com.amedigital.wallet.service.fastcash.response.FastCashErrorResponse;

import reactor.core.publisher.Mono;

@Service
public class FastCashServiceImpl implements FastCashService {

	private static final Logger LOG = LoggerFactory.getLogger(FastCashServiceImpl.class);
	
	private static final String CASHOUT = "cashout";
	private static final String CASHIN = "cashin";
	
    private final WebClient client;
    private final FastCashConfig fastCashConfig;

    @Autowired
    public FastCashServiceImpl(final WebClient client, final FastCashConfig config) {
        this.client = client;
        this.fastCashConfig = config;
    }
	
	@Override
	public Mono<CashOutResponse> requestCashOutToBankAccount(CashOutRequest cashoutRequest) {
		LOG.info("Iniciando a transferencia na FastCash  com o payoutRequest [{}]", cashoutRequest);

        return client.post()
                .uri(String.format("%s/%s", fastCashConfig.getUrl(), CASHOUT))
                .accept(APPLICATION_JSON)
                .body(Mono.just(cashoutRequest), CashOutRequest.class)
                .exchange()
                .timeout(Duration.ofSeconds(30))
                .flatMap(res -> {
                	
                	LOG.info("Resposta da Fastcash na autorizacao, Status: [{}]", res.statusCode());
                	if (!res.statusCode().is2xxSuccessful()) {
						return res.bodyToMono(CashOutResponse.class)
								.flatMap(cashoutResponse -> Mono.error(new AmeInternalException(cashoutResponse.getError_description())))
								.switchIfEmpty(Mono.just(new CashOutResponse()))
								.flatMap($ ->  Mono.error(new AmeInternalException("erro desconhecido.")));
					}
                	return Mono.just(new CashOutResponse());
                });
	}

	@Override
	public Mono<CashInResponse> requestCashInFromBankAccount(CashInRequest cashInRequest) {
		LOG.info("Iniciando a solicitacao de BANK_CASH_IN na FastCash  com o cashInRequest [{}]", cashInRequest);

        return client.post()
                .uri(String.format("%s/%s", fastCashConfig.getUrl(), CASHIN))
                .accept()
                .body(Mono.just(cashInRequest), CashInRequest.class)
                .exchange()
                .timeout(Duration.ofSeconds(30))
                .flatMap(res -> {
                	
                	LOG.info("Resposta da Fastcash na autorizacao de Cash In, Status: [{}]", res.statusCode());
                	
                	if (res.statusCode().is2xxSuccessful()) {
                        return res.bodyToMono(CashInResponse.class);
                    } else {
						return res.bodyToMono(FastCashErrorResponse.class)
								.flatMap(errorResponse -> Mono.error(new AmeInternalException(errorResponse.getError_description())))
								.switchIfEmpty(Mono.just(new FastCashErrorResponse()))
								.flatMap($ ->  Mono.error(new AmeInternalException("erro desconhecido.")));
					}
                });
	}
}
