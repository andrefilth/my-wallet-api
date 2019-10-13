package com.amedigital.wallet.service.impl;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.endoint.response.v3.query.SimpleReleaseStatementResponse;
import com.amedigital.wallet.model.OrderItem;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.OrderService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final WalletRepository walletRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository repository, WalletRepository walletRepository) {
        this.repository = repository;
        this.walletRepository = walletRepository;
    }

    @Override
    public Mono<Order> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    @Override
    public Mono<SecondaryOrder> findByOrderReferenceAndSecondaryId(String orderReference, String secondaryId) {
        if (!Strings.isNullOrEmpty(secondaryId) && !Strings.isNullOrEmpty(orderReference)) {
            return repository.findByOrderReferenceAndSecondaryId(orderReference, secondaryId);
        }

        return Mono.empty();
    }

    @Override
    public Flux<SecondaryOrder> findByOrderReference(String orderReference) {
        if (!Strings.isNullOrEmpty(orderReference)) {
            return repository.findByOrderReference(orderReference);
        }

        return Flux.empty();
    }

    @Override
    public Flux<OrderItem> findBy(Long limit,
                                  Long offset,
                                  List<OrderType> orderTypes,
                                  Optional<String> referenceOrderUuid,
                                  Optional<String> walletUuid,
                                  Optional<String> cancellationReference,
                                  Optional<LocalDate> beginDate,
                                  Optional<LocalDate> endDate) {

        return walletUuid.map(s -> walletRepository.findByUuid(s)
                .flatMapMany(wallet -> repository.findBy(limit, offset, orderTypes, referenceOrderUuid, wallet.getId(), cancellationReference, beginDate, endDate)))
                .orElseGet(() -> repository.findBy(limit, offset, orderTypes, referenceOrderUuid, Optional.empty(), cancellationReference, beginDate, endDate));
    }


    @Override
    public Flux<SimpleReleaseStatementResponse> findReleaseStatementBy(List<OrderStatus> orderStatus, List<OrderType> orderTypes, LocalDate beginDate, LocalDate endDate) {
        return repository.findReleaseStatementBy(orderStatus, orderTypes, beginDate, endDate);
    }

    @Override
    public Mono<OrderItem> findReleaseOrderByPrimaryOrder(String primaryOrderUuid) {
        return findBy(1L, 0L, Collections.singletonList(OrderType.RELEASE), Optional.of(primaryOrderUuid), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())
                .collectList()
                .flatMap(s -> s.isEmpty() ? Mono.empty() : Mono.just(s.get(0)));
    }
}
