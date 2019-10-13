package com.amedigital.wallet.service;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface WalletOperationAttributeService {

    Mono<Map<String, Object>> findById(final String id);

    Mono<Map<String, Object>> save(String orderDetailUuid, Map<String, Object> walletOperationAttribute);
}
