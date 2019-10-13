package com.amedigital.wallet.repository.impl;

import com.amedigital.wallet.constants.enuns.OrderLevel;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.endoint.response.v3.query.SimpleReleaseStatementResponse;
import com.amedigital.wallet.model.OrderItem;
import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.PrimaryOrder;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.ActionRepository;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.repository.OwnerRepository;
import com.amedigital.wallet.repository.TransactionRepository;
import com.amedigital.wallet.repository.mappers.query.SimpleReleaseStatementMapper;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class AuroraOrderRepository implements OrderRepository {

    private static final String SQL_ORDER_FILTER = ClasspathSqlLocator.findSqlOnClasspath("sql.order.order-filter");
    private static final String SQL_ORDER_FILTER_BY_DATE = ClasspathSqlLocator.findSqlOnClasspath("sql.order.order-filter-date");
    private static final String SQL_FILTER_BY_REFERENCE  = ClasspathSqlLocator.findSqlOnClasspath("sql.order.order-find-by-reference-uuid");
    private static final String SQL_FILTER_BY_GATEWAY_CANCEL_REFERENCE  = ClasspathSqlLocator.findSqlOnClasspath("sql.order.order-filter-cancellation-reference");

    private final Jdbi jdbi;
    private final ActionRepository actionRepository;
    private final TransactionRepository transactionRepository;
    private final Scheduler jdbcScheduler;
    private final OwnerRepository ownerRepository;

    @Autowired
    public AuroraOrderRepository(Jdbi jdbi,
                                 ActionRepository actionRepository,
                                 TransactionRepository transactionRepository,
                                 Scheduler scheduler,
                                 OwnerRepository ownerRepository) {
        this.jdbi = jdbi;
        this.actionRepository = actionRepository;
        this.transactionRepository = transactionRepository;
        this.jdbcScheduler = scheduler;
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Mono<Order> findByUuid(String uuid) {
        return async(() -> {
            String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.order.order-find-with-action");

            return jdbi.withHandle(handle -> {
                Optional<Order> optionalOrder = handle.createQuery(sql)
                        .bind("order_uuid", uuid)
                        .mapTo(Order.class)
                        .findFirst();

                return optionalOrder.map(order -> {
                    List<Transaction> transactions = transactionRepository.findByOrderId(handle, order.getId());

                    if (OrderLevel.PRIMARY.equals(order.getOrderLevel())) {
                        PrimaryOrder primaryOrder = (PrimaryOrder) order;
                        return (Order) primaryOrder.copy().setTransactions(transactions).build();
                    } else {
                        SecondaryOrder secondaryOrder = (SecondaryOrder) order;
                        return (Order) secondaryOrder.copy().setTransactions(transactions).build();
                    }

                });
            });
        }).flatMap(Mono::justOrEmpty);
    }

    @Override
    public Mono<Order> save(Order order) {
        return async(() -> {
            String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.order.order-insert");

            return jdbi.withHandle(handle -> {
                handle.execute("update tb_order set latest = false where uuid = ?", order.getUuid());

                Long orderId = handle.createUpdate(sql)
                        .bind("uuid", order.getUuid())
                        .bind("type", order.getType())
                        .bind("status", order.getStatus())
                        .bind("total_amount_in_cents", order.getTotalAmountInCents())
                        .bind("title", order.getTitle())
                        .bind("description", order.getDescription())
                        .bind("order_detail_uuid", order.getOrderDetailUuid())
                        .bind("authorization_method", order.getAuthorizationMethod())
                        .bind("created_by_wallet_id", order.getCreatedByWalletId())
                        .bind("reference_order_uuid", order.getReferenceOrderUuid())
                        .bind("secondary_id", order.getSecondaryId())
                        .bind("payment_methods", order.getPaymentMethods().stream().distinct().collect(Collectors.joining(",")))
                        .bind("nsu", order.getNsu())
                        .bind("created_at", order.getCreatedAt())
                        .bind("updated_at", ZonedDateTime.now())
                        .executeAndReturnGeneratedKeys("id")
                        .mapTo(Long.class)
                        .findOnly();

                Action action = actionRepository.save(handle, orderId, order.getUuid(), order.getAction());

                List<Transaction> transactions = transactionRepository.save(handle, orderId, order.getUuid(), action.getId(), order.getTransactions());

                Owner owner = ownerRepository.findByWalletId(handle, order.getCreatedByWalletId());

                return (Order) order.copy()
                        .setId(orderId)
                        .setAction(action)
                        .setTransactions(transactions)
                        .setCreatedByOwner(owner)
                        .build();
            });
        });
    }

    @Override
    public Flux<SecondaryOrder> findByOrderReference(String uuid) {
        return asyncFlux(() -> {
            String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.order.order-find-by-orderuuid");

            return jdbi.withHandle(handle -> {
                List<Order> orders = handle.createQuery(sql)
                        .bind("order_uuid", uuid)
                        .mapTo(Order.class)
                        .list();

                return orders.stream()
                        .map(order -> {
                            var transactions = transactionRepository.findByOrderId(handle, order.getId());
                            return (SecondaryOrder) order.copy().setTransactions(transactions).build();
                        });
            });
        });
    }

    @Override
    public Mono<SecondaryOrder> findByOrderReferenceAndSecondaryId(String uuid, String secondaryId) {

        return async(() -> {
            String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.order.order-find-by-orderuuid-and-refundId");

            return jdbi.withHandle(handle -> {
                Optional<Order> optionalOrder = handle.createQuery(sql)
                        .bind("order_uuid", uuid)
                        .bind("secondary_id", secondaryId)
                        .mapTo(Order.class)
                        .findFirst();

                return optionalOrder.map(order -> {
                    List<Transaction> transactions = transactionRepository.findByOrderId(handle, order.getId());
                    return (SecondaryOrder) order.copy().setTransactions(transactions).build();
                });
            });
        }).flatMap(Mono::justOrEmpty);

    }

    @Override
    public Flux<OrderItem> findBy(Long limit,
                                  Long offset,
                                  List<OrderType> orderTypes,
                                  Optional<String> referenceOrderUuid,
                                  Optional<Long> walletId,
                                  Optional<String> cancellationReference,
                                  Optional<LocalDate> beginDate,
                                  Optional<LocalDate> endDate
                                  ) {

        if(referenceOrderUuid.isPresent()) {
            return asyncFlux(() -> jdbi.withHandle(handle -> {
                String referenceOrderUuidValue = referenceOrderUuid.get();
                        return handle.createQuery(SQL_FILTER_BY_REFERENCE)

                                .bind("reference_order_uuid", referenceOrderUuidValue)
                                .bindList("orderTypes",
                                        Optional.ofNullable(orderTypes)
                                                .filter(type -> !type.isEmpty())
                                                .orElse(Arrays.asList(OrderType.values()))
                                                .stream().map(OrderType::name).collect(Collectors.toList()))
                                .mapTo(OrderItem.class)
                                .list()
                                .stream();
                    })
            ).flatMap(Mono::justOrEmpty);

        }

        if(cancellationReference.isPresent()) {
            return asyncFlux(() -> jdbi.withHandle(handle -> {
                        String gatewayCancellationReference = cancellationReference.get();
                        return handle.createQuery(SQL_FILTER_BY_GATEWAY_CANCEL_REFERENCE)

                                .bind("gateway_cancellation_reference", gatewayCancellationReference)
                                .mapTo(OrderItem.class)
                                .list()
                                .stream();
                    })
            ).flatMap(Mono::justOrEmpty);
        }


        if(beginDate.isPresent() && endDate.isPresent()) {
            return asyncFlux(() -> jdbi.withHandle(handle -> {
                        return handle.createQuery(SQL_ORDER_FILTER_BY_DATE)
                                .bind("begin_date",  beginDate.get())
                                .bind("end_date", endDate.get().plusDays(1))
                                .bindList("orderTypes",
                                        Optional.ofNullable(orderTypes)
                                                .filter(type -> !type.isEmpty())
                                                .orElse(Arrays.asList(OrderType.values()))
                                                .stream().map(OrderType::name).collect(Collectors.toList()))
                                .mapTo(OrderItem.class)
                                .list()
                                .stream();
                    })
            ).flatMap(Mono::justOrEmpty);
        }


            return asyncFlux(() -> jdbi.withHandle(handle -> handle.createQuery(SQL_ORDER_FILTER)
                    .bind("limit", limit)
                    .bind("offset", offset)
                    .bind("reference_order_uuid", referenceOrderUuid.filter(ref -> !ref.isEmpty()))
                    .bind("wallet_id", walletId)
                    .bind("gateway_cancellation_reference", cancellationReference)
                    .bindList("orderTypes",
                            Optional.ofNullable(orderTypes)
                                    .filter(type -> !type.isEmpty())
                                    .orElse(Arrays.asList(OrderType.values()))
                                    .stream().map(OrderType::name).collect(Collectors.toList()))
                    .mapTo(OrderItem.class)
                    .list()
                    .stream())
            ).flatMap(Mono::justOrEmpty);
    }

    @Override
    public Flux<SimpleReleaseStatementResponse> findReleaseStatementBy(List<OrderStatus> status, List<OrderType> orderTypes, LocalDate beginDate, LocalDate endDate) {

        return Flux.create(sink -> {
            try (Handle handle = jdbi.open()) {
                handle.createQuery(SQL_ORDER_FILTER_BY_DATE)
                        .bind("begin_date",  beginDate)
                        .bind("end_date", endDate.plusDays(1))
                        .bindList("order_status",
                                Optional.ofNullable(status)
                                        .filter(type -> !type.isEmpty())
                                        .orElse(Arrays.asList(OrderStatus.values()))
                                        .stream().map(OrderStatus::name).collect(Collectors.toList()))
                        .bindList("orderTypes",
                                Optional.ofNullable(orderTypes)
                                        .filter(type -> !type.isEmpty())
                                        .orElse(Arrays.asList(OrderType.values()))
                                        .stream().map(OrderType::name).collect(Collectors.toList()))
                        .mapTo(SimpleReleaseStatementResponse.class)
                        .useStream(s -> {
                            s.forEach(sink::next);
                            sink.complete();
                        });

            } catch (Exception e){
                sink.error(e);
            }
        });

    }

    private <T> Flux<T> asyncFlux(Supplier<Stream<? extends T>> supplier) {
        return Flux.fromStream(supplier)
                .subscribeOn(jdbcScheduler)
                .publishOn(Schedulers.parallel());

    }

    private <T> Mono<T> async(Callable<T> supplier) {
        return Mono.fromCallable(supplier)
                .subscribeOn(jdbcScheduler)
                .publishOn(Schedulers.parallel());
    }
}