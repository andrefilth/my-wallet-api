package com.amedigital.wallet.repository.impl;

import com.amedigital.wallet.model.Balance;
import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.WalletBalance;
import com.amedigital.wallet.repository.OwnerRepository;
import com.amedigital.wallet.repository.WalletRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.ZonedDateTime;
import java.util.concurrent.Callable;

import static com.amedigital.wallet.repository.constants.Query.Wallet.*;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    private static final Logger LOG = LoggerFactory.getLogger(WalletRepositoryImpl.class);

    private final Jdbi jdbi;
    private final Scheduler jdbcScheduler;
    private final OwnerRepository ownerRepository;

    @Autowired
    public WalletRepositoryImpl(Jdbi jdbi, Scheduler jdbcScheduler, OwnerRepository ownerRepository) {
        this.jdbi = jdbi;
        this.jdbcScheduler = jdbcScheduler;
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Mono<Wallet> insert(Wallet wallet) {
        return async(() -> {
            final String sql = "INSERT INTO tb_wallet(owner_id, uuid, type, name, main, created_at, updated_at) " +
                    "VALUES (:ownerId, :uuid, :type, :name, :main, :createdAt, :updatedAt)";

            return jdbi.withHandle(handle -> {
                Owner persistedOwner = ownerRepository.insert(handle, wallet.getOwner());

                Long walletId = handle.createUpdate(sql)
                        .bind("ownerId", persistedOwner.getId().get())
                        .bind("uuid", wallet.getUuid())
                        .bind("type", wallet.getType())
                        .bind("name", wallet.getName())
                        .bind("main", wallet.isMain())
                        .bind("createdAt", ZonedDateTime.now())
                        .bind("updatedAt", ZonedDateTime.now())
                        .executeAndReturnGeneratedKeys("id")
                        .mapTo(Long.class)
                        .findOnly();

                return wallet.copy()
                        .setId(walletId)
                        .setOwner(persistedOwner)
                        .build();
            });
        });
    }

    @Override
    public Mono<Wallet> findByUuid(String uuid) {
        return async(() -> this.jdbi.withHandle(handle ->
                handle.createQuery(GET_WALLET_BY_UUID)
                        .bind("uuid", uuid)
                        .mapTo(Wallet.class)
                        .findFirst()
                        .map(wallet -> wallet.copy()
                                .setBalance(findBalanceByWalletId(handle, wallet.getId().get()))
                                .build())))
                .flatMap(Mono::justOrEmpty);
    }

    @Override
    public Mono<Wallet> findById(Long id) {
        return async(() -> this.jdbi.withHandle(handle ->
                handle.createQuery(GET_WALLET_BY_ID)
                        .bind("id", id)
                        .mapTo(Wallet.class)
                        .findFirst()
                        .map(wallet -> wallet.copy()
                                .setBalance(findBalanceByWalletId(handle, wallet.getId().get()))
                                .build())
        )).flatMap(Mono::justOrEmpty);
    }


    @Override
    public Mono<Wallet> findByOwnerUuid(String ownerUuid) {
        return async(() -> this.jdbi.withHandle(handle ->
                handle.createQuery(GET_PRINCIPAL_WALLET_BY_EXTERNAL_OWNER_UUID)
                        .bind("uuid", ownerUuid)
                        .mapTo(Wallet.class)
                        .findFirst()
                        .map(wallet -> wallet.copy()
                                .setBalance(findBalanceByWalletId(handle, wallet.getId().get()))
                                .build()))
        ).flatMap(Mono::justOrEmpty);
    }


    @Override
    public Mono<WalletBalance> findBalanceByWalletId(Long id) {
        return async(() -> jdbi.withHandle(handle -> findBalanceByWalletId(handle, id)));
    }

    private boolean isMerchantId(Long walletId) {
        return walletId.equals(1L) ||
                walletId.equals(71416L) ||
                walletId.equals(71432L) ||
                walletId.equals(445209L) ||
                walletId.equals(445214L) ||
                walletId.equals(920519L);
    }

    private WalletBalance findBalanceByWalletId(Handle handle, Long walletId) {
        if (isMerchantId(walletId)) {
            var emptyBalance = Balance.emptyBalance();

            new WalletBalance(emptyBalance, emptyBalance);
        }
        var cashBalance = handle.createQuery(ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.cash-transaction-balance"))
                .bind("wallet_id", walletId)
                .mapTo(Balance.class)
                .findFirst()
                .orElse(Balance.builder().build());

        var cashBackBalance = handle.createQuery(ClasspathSqlLocator.findSqlOnClasspath("sql.transaction.cash-back-transaction-balance"))
                .bind("wallet_id", walletId)
                .mapTo(Balance.class)
                .findFirst()
                .orElse(Balance.builder().build());

        return new WalletBalance(cashBalance, cashBackBalance);
    }

    @Override
    public Mono<Wallet> update(Wallet wallet) {
        return async(() -> jdbi.withHandle(handle -> {
            ownerRepository.update(handle, wallet.getOwner());

            handle.createUpdate(UPDATE_WALLET)
                    .bindBean(wallet)
                    .execute();

            return wallet;
        }));
    }

    private <T> Mono<T> async(Callable<T> supplier) {
        return Mono.fromCallable(supplier)
                .subscribeOn(jdbcScheduler)
                .publishOn(Schedulers.parallel());
    }

}
