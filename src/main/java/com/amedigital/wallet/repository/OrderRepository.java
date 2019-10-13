package com.amedigital.wallet.repository;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.endoint.response.v3.query.SimpleReleaseStatementResponse;
import com.amedigital.wallet.model.OrderItem;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Mono<Order> findByUuid(String uuid);

    Mono<Order> save(Order order);

    Flux<SecondaryOrder> findByOrderReference(String uuid);

    Mono<SecondaryOrder> findByOrderReferenceAndSecondaryId(String uuid, String secondaryId);

    Flux<OrderItem> findBy(Long limit,
                           Long offset,
                           List<OrderType> orderTypes,
                           Optional<String> referenceOrderUuid,
                           Optional<Long> walletUuid,
                           Optional<String> cancellationReference,
                           Optional<LocalDate> beginDate,
                           Optional<LocalDate> endDate
                           );

    Flux<SimpleReleaseStatementResponse> findReleaseStatementBy(List<OrderStatus> status, List<OrderType> orderTypes, LocalDate beginDate, LocalDate endDate);
}