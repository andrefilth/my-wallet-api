package com.amedigital.wallet.repository;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface DynamoRepository {

    Mono<Map<String, Object>> save(String orderDetailUuid, Map<String, Object> walletOperationAttribute);

    Mono<Map<String, Object>> findById(String id);

}
