package com.amedigital.wallet.repository.impl;

import com.amedigital.wallet.endoint.response.SimpleCreditCardResponse;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.CreditCard;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Repository
public class CreditCardTokenRepositoryImpl implements com.amedigital.wallet.repository.CreditCardTokenRepository {

    private static final String CARD_ID = "card_id";

    private static final String CREDIT_CARD_INSERT_SQL = ClasspathSqlLocator
            .findSqlOnClasspath("sql.creditcard.creditcard-insert");

    private static final String CREDIT_CARD_UPDATE_SQL = ClasspathSqlLocator
            .findSqlOnClasspath("sql.creditcard.creditcard-update");

    private static final String CREDIT_CARDS_FIND_SQL = ClasspathSqlLocator
            .findSqlOnClasspath("sql.creditcard.creditcards-find-by-wallet-id");

    private static final String CREDIT_CARD_FIND_BY_CARD_ID_SQL = ClasspathSqlLocator
            .findSqlOnClasspath("sql.creditcard.creditcards-find-by-card-id");

    private static final String CREDIT_CARD_UPDATE_MAIN_CARD_SQL = ClasspathSqlLocator
            .findSqlOnClasspath("sql.creditcard.creditcard-update-main-card");

    private static final String CREDIT_CARD_UPDATE_SELECT_NEW_MAIN_CARD_SQL = ClasspathSqlLocator
            .findSqlOnClasspath("sql.creditcard.creditcard-update-select-new-main-card");
    private static final String FIND_CARD_ID_BY_UUID = "select id from tb_creditcard where uuid = :uuid";

    private final Jdbi jdbi;

    private final Scheduler jdbcScheduler;

    @Autowired
    public CreditCardTokenRepositoryImpl(Jdbi jdbi, Scheduler jdbcScheduler) {
        this.jdbi = jdbi;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Mono<CreditCard> save(CreditCard card) {
        return async(() -> jdbi.inTransaction(handle -> {
            handle.createUpdate(CREDIT_CARD_UPDATE_SQL)
                    .bindBean(card)
                    .execute();


            return cardIdByUUID(card.getUuid(), handle).flatMap(cid -> {
                updateMainCard(card, cid, handle);
                return findCardWith(cid, handle);
            });
        })).flatMap(Mono::justOrEmpty);
    }

    @Override
    public Mono<CreditCard> create(final CreditCard card) {

        return async(() -> jdbi.inTransaction(
                handle -> {
                    try {
                        return handle.createUpdate(CREDIT_CARD_INSERT_SQL)
                                .bindBean(card)
                                .executeAndReturnGeneratedKeys("id")
                                .mapTo(Long.class)
                                .findFirst()
                                .flatMap(cid -> {
                                    updateMainCard(card, cid, handle);
                                    return findCardWith(cid, handle);
                                });
                    } catch (UnableToExecuteStatementException e) {
                        return handleThrownException(e);
                    }
                }))
                .flatMap(Mono::justOrEmpty);
    }

    @Override
    public Mono<CreditCard> findByCardUUID(String cardUUID) {
        return async(() ->
                jdbi.withHandle(handle ->
                        cardIdByUUID(cardUUID, handle).flatMap(cid -> findCardWith(cid, handle))
                )).flatMap(Mono::justOrEmpty);
    }


    @Override
    public Flux<CreditCard> findByWalletId(Long walletId) {
        return asyncFlux(() -> jdbi.withHandle(handle ->
                handle.createQuery(CREDIT_CARDS_FIND_SQL)
                        .bind("wallet_id", walletId)
                        .mapTo(CreditCard.class)
                        .list().stream()));
    }

    /**
     * TODO: PROVISORIO!!!
     * @param orderUuid
     * @return
     */
    @Override
    public Mono<SimpleCreditCardResponse> findByOrderUuid(final String orderUuid) {
        return async(() -> {
            String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.creditcard.creditcard-find-by-order-uuid");

            return jdbi.withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("order_uuid", orderUuid)
                            .mapTo(SimpleCreditCardResponse.class)
                            .findFirst());
        }).flatMap(Mono::justOrEmpty);
    }

    private Optional<Long> cardIdByUUID(String uuid, Handle handle) {
        return handle.createQuery(FIND_CARD_ID_BY_UUID)
                .bind("uuid", uuid)
                .mapTo(Long.class).findFirst();
    }

    private Optional<CreditCard> findCardWith(Long cardId, Handle handle) {
        return handle.createQuery(CREDIT_CARD_FIND_BY_CARD_ID_SQL)
                .bind(CARD_ID, cardId)
                .mapTo(CreditCard.class)
                .findFirst();
    }

    private void updateMainCard(CreditCard card, Long cardId, Handle handle) {
        if (card.getMain())
            handle.createUpdate(CREDIT_CARD_UPDATE_MAIN_CARD_SQL)
                    .bind("walletId", card.getWalletId())
                    .bind("cardId", cardId != null ? cardId : card.getId())
                    .execute();
        else {
            handle.createUpdate(CREDIT_CARD_UPDATE_SELECT_NEW_MAIN_CARD_SQL)
                    .bind("walletId", card.getWalletId())
                    .execute();
        }
    }

    private Optional<CreditCard> handleThrownException(UnableToExecuteStatementException e) {
        if (e.getMessage().contains("Duplicate") && e.getMessage().contains("uuid")) {
            throw new AmeInvalidInputException("uuid_unique", "uuid is already in use");
        }

        if (e.getMessage().contains("Duplicate") && e.getMessage().contains("token")) {
            throw new AmeInvalidInputException("uuid_unique", "uuid is already in use");
        }

        throw e;
    }

    private <T> Mono<T> async(Callable<T> callable) {
        return Mono.fromCallable(callable)
                .subscribeOn(jdbcScheduler)
                .publishOn(Schedulers.parallel());
    }

    private <T> Flux<T> asyncFlux(Supplier<Stream<? extends T>> supplier) {
        return Flux.fromStream(supplier)
                .subscribeOn(jdbcScheduler)
                .publishOn(Schedulers.parallel());

    }
}
