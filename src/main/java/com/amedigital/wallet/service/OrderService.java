package com.amedigital.wallet.service;

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

public interface OrderService {

    Mono<Order> findByUuid(String uuid);

    Mono<SecondaryOrder> findByOrderReferenceAndSecondaryId(String orderReference, String secondaryId);

    Flux<SecondaryOrder> findByOrderReference(String orderReference);

    Flux<OrderItem> findBy(Long limit,
                           Long offset,
                           List<OrderType> orderTypes,
                           Optional<String> referenceOrderUuid,
                           Optional<String> walletUuid,
                           Optional<String> cancellationReference,
                           Optional<LocalDate> beginDate,
                           Optional<LocalDate> endDate
                           );

    Flux<SimpleReleaseStatementResponse> findReleaseStatementBy(List<OrderStatus> orderStatus,
                                                                List<OrderType> orderTypes,
                                                                LocalDate beginDate,
                                                                LocalDate endDate
    );


    Mono<OrderItem> findReleaseOrderByPrimaryOrder(String primaryOrderUuid);

}
