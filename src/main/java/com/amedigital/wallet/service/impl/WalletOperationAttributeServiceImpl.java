package com.amedigital.wallet.service.impl;

import com.amedigital.wallet.repository.DynamoRepository;
import com.amedigital.wallet.service.WalletOperationAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class WalletOperationAttributeServiceImpl implements WalletOperationAttributeService {

    private final DynamoRepository dynamoRepository;

    @Autowired
    public WalletOperationAttributeServiceImpl(DynamoRepository dynamoRepository) {
        this.dynamoRepository = dynamoRepository;
    }

    @Override
    public Mono<Map<String, Object>> findById(String id) {
        return dynamoRepository.findById(id);
    }

    @Override
    public Mono<Map<String, Object>> save(String orderDetailUuid, Map<String, Object> walletOperationAttribute) {
        return dynamoRepository.save(orderDetailUuid, walletOperationAttribute);
    }
}
