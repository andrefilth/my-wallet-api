package com.amedigital.wallet.service.state.order.release.storecashin;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.CashStatus;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.StoreCashInOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import static com.amedigital.wallet.constants.Constants.RELEASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.authorizedMessageException;

@Service
public class AuthorizedStoreCashInReleaseState implements SecondaryOrderState<StoreCashInOrder, ReleaseOrder> {

    private final OrderRepository repository;

    @Autowired
    public AuthorizedStoreCashInReleaseState(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<ReleaseOrder> create(StoreCashInOrder order, ReleaseOrder releaseOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                authorizedMessageException("criar", RELEASE_ORDER_TYPE)));
    }

    @Override
    public Mono<ReleaseOrder> authorize(StoreCashInOrder order, ReleaseOrder releaseOrder) {
        return Mono.just(releaseOrder);
    }

    @Override
    public Mono<ReleaseOrder> finish(StoreCashInOrder order, ReleaseOrder releaseOrder) {

        if (!order.getUuid().equals(releaseOrder.getReferenceOrderUuid())) {
            throw new AmeInvalidInputException("wallet_validation", "A ordem de release não corresponde a ordem capturada");
        }

        ZonedDateTime now = ZonedDateTime.now();

        var transactions = releaseOrder.getTransactions()
                .stream()
                .map(t -> (Transaction) ((CashTransaction) t).copy()
                        .setStatus(TransactionStatus.CAPTURED)
                        .setCreatedAt(now)
                        .setUpdatedAt(now)
                        .setCashStatus(CashStatus.CAPTURED)
                        .setCashCreatedAt(now)
                        .setCashUpdatedAt(now)
                        .build())
                .collect(Collectors.toList());

        var action = new Action.Builder()
                .setType(ActionType.RELEASE)
                .setParentId(releaseOrder.getAction().getId())
                .setCreatedAt(now)
                .build();

        var releaseOrderAuthorized = releaseOrder.copy()
                .setStatus(OrderStatus.RELEASED)
                .setUpdatedAt(now)
                .setAction(action)
                .setTransactions(transactions)
                .build();

        return repository
                .save(releaseOrderAuthorized)
                .cast(ReleaseOrder.class);
    }
    
    @Override
    public Mono<ReleaseOrder> cancel(StoreCashInOrder order, ReleaseOrder releaseOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                authorizedMessageException("cancelar", RELEASE_ORDER_TYPE)));
    }

}
