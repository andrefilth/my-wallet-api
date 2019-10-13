package com.amedigital.wallet.repository.impl;

import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.repository.OwnerRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;

import static com.amedigital.wallet.repository.constants.Query.Owner.*;

@Repository
public class OwnerRepositoryImpl implements OwnerRepository {

    private final Jdbi jdbi;
    private final Scheduler jdbcScheduler;

    @Autowired
    public OwnerRepositoryImpl(Jdbi jdbi, Scheduler jdbcScheduler) {
        this.jdbi = jdbi;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Owner insert(Handle handle, Owner owner) {
        final String sql = "INSERT INTO tb_owner(uuid, document, document_type, name, email, external_id) " +
                "VALUES (:uuid, :document, :documentType, :name, :email, :externalId)";

        Long ownerId = handle.createUpdate(sql)
                .bind("uuid", owner.getUuid())
                .bind("document", owner.getDocument())
                .bind("documentType", owner.getDocumentType())
                .bind("name", owner.getName())
                .bind("email", owner.getEmail())
                .bind("externalId", owner.getExternalId())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(Long.class)
                .findOnly();

        return owner.copy()
                .setId(ownerId)
                .build();
    }

    @Override
    public Owner update(Handle handle, Owner owner) {
        handle.createUpdate(UPDATE_OWNER)
                .bindBean(owner)
                .execute();

        return owner;
    }


    @Override
    public Mono<Owner> findByDocument(String document) {
        return async(() ->
                jdbi.withHandle(handle -> handle.createQuery(FIND_OWNER_BY_DOCUMENT)
                        .bind("document", document)
                        .mapTo(Owner.class)
                        .findFirst())
        ).flatMap(Mono::justOrEmpty);
    }

    @Override
    public Mono<Owner> findByEmail(String email) {
        return async(() ->
                jdbi.withHandle(handle ->
                        handle.createQuery(FIND_OWNER_BY_EMAIL)
                                .bind("email", email)
                                .mapTo(Owner.class)
                                .findFirst())
        ).flatMap(Mono::justOrEmpty);
    }

    @Override
    public Mono<Owner> findByWalletId(Long walletId) {
        String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.owner.owner-find-by-walletid");

        return async(() ->
                jdbi.withHandle(handle ->
                        handle.createQuery(sql)
                                .bind("walletId", walletId)
                                .mapTo(Owner.class)
                                .findFirst())
        ).flatMap(Mono::justOrEmpty);
    }

    @Override
    public Owner findByWalletId(Handle handle, Long walletId) {
        String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.owner.owner-find-by-walletid");

        return handle.createQuery(sql)
                .bind("walletId", walletId)
                .mapTo(Owner.class)
                .findFirst()
                .orElse(null);
    }

    private <T> Mono<T> async(Callable<T> supplier) {
        return Mono.fromCallable(supplier)
                .subscribeOn(jdbcScheduler)
                .publishOn(Schedulers.parallel());
    }
}