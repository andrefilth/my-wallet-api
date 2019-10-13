package com.amedigital.wallet.repository.impl;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.model.StatementItem;
import com.amedigital.wallet.model.TransactionDataItem;
import com.amedigital.wallet.repository.StatementRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Repository
public class StatementRepositoryImpl implements StatementRepository {

    private static final String SQL_ORDERS_STATEMENT = ClasspathSqlLocator.findSqlOnClasspath("sql.order.statement");
    private static String transactionDataSql = ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.transaction-data");

    private static final List<OrderStatus> DEFAULT_ORDER_STATUS =
            Arrays.asList(
                    OrderStatus.AUTHORIZED, OrderStatus.CAPTURED, OrderStatus.REFUNDED,
                    OrderStatus.PENDING, OrderStatus.RELEASED, OrderStatus.CANCELLED,
                    OrderStatus.DENIED);

    private static final List<TransactionType> DEFAULT_TRANSACTION_TYPE = Arrays.asList(TransactionType.CREDIT, TransactionType.DEBIT);

    private final Scheduler jdbcScheduler;
    private final Jdbi jdbi;

    @Autowired
    public StatementRepositoryImpl(Scheduler jdbcScheduler, Jdbi jdbi) {
        this.jdbcScheduler = jdbcScheduler;
        this.jdbi = jdbi;
    }

    @Override
    public Flux<StatementItem> getWalletStatement(final long walletId,
                                                  final int size,
                                                  final int offset,
                                                  List<TransactionType> types,
                                                  List<OrderStatus> statusList) {

        if (!statusList.isEmpty()) {
            if (!types.isEmpty() && types.contains(TransactionType.CREDIT)) {
                statusList.add(OrderStatus.RELEASED);
            }
        }

        return asyncFlux(() -> jdbi.withHandle(handle -> handle.createQuery(SQL_ORDERS_STATEMENT)
                .bind("wallet_id", walletId)
                .bind("size", size)
                .bind("offset", offset * size)
                .bindList("orderStatus", !statusList.isEmpty() ? statusList : DEFAULT_ORDER_STATUS)
                .bindList("transactionType", !types.isEmpty() ? types : DEFAULT_TRANSACTION_TYPE)
                .mapTo(StatementItem.class)
                .list()
                .stream()));
    }

    @Override
    public Flux<TransactionDataItem> findTransactionsDataBy(OrderStatus orderStatus,
                                                            OrderType orderType,
                                                            String walletId,
                                                            ZonedDateTime dateStart,
                                                            ZonedDateTime dateEnd) {
        return asyncFlux(() -> jdbi.withHandle(handle ->
                handle.createQuery(transactionDataSql)
                        .bind("order_status", orderStatus)
                        .bind("order_type", orderType)
                        .bind("wallet_id", walletId)
                        .bind("date_start", dateStart)
                        .bind("date_end", dateEnd)
                        .mapTo(TransactionDataItem.class)
                        .list()
                        .stream()));
    }

    private <T> Flux<T> asyncFlux(Supplier<Stream<? extends T>> supplier) {
        return Flux.fromStream(supplier)
                .subscribeOn(jdbcScheduler)
                .publishOn(Schedulers.parallel());

    }
}
