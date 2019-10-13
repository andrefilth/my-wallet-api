package com.amedigital.wallet.service.state.order.release.purchase;

import com.amedigital.wallet.constants.enuns.CashStatus;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import static com.amedigital.wallet.constants.Constants.RELEASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.authorizedMessageException;
import static com.amedigital.wallet.constants.enuns.ActionType.RELEASE;

@Service
public class AuthorizedPurchaseReleaseState implements SecondaryOrderState {

    private final OrderRepository repository;

    @Autowired
    public AuthorizedPurchaseReleaseState(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<SecondaryOrder> create(Order order, SecondaryOrder secondaryOrder) {
        throw new AmeInvalidInputException("wallet_validation", authorizedMessageException("criar", RELEASE_ORDER_TYPE));
    }

    @Override
    public Mono<SecondaryOrder> authorize(Order order, SecondaryOrder secondaryOrder) {
        return Mono.just(secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> finish(Order order, SecondaryOrder secondaryOrder) {
        var releaseOrder = (ReleaseOrder) secondaryOrder;

        if (!order.getUuid().equals(releaseOrder.getReferenceOrderUuid())) {
            throw new AmeInvalidInputException("wallet_validation", "A ordem de release não corresponde a ordem capturada");
        }

        ZonedDateTime now = ZonedDateTime.now();

        var transactions = releaseOrder.getTransactions()
                .stream()
                .map(t ->  t.getReleaseDate() == null || ChronoUnit.DAYS.between(t.getReleaseDate().toLocalDate(), now.toLocalDate()) >= 0 ?
                        (Transaction) ((CashTransaction) t).copy()
                                .setStatus(TransactionStatus.CAPTURED)
                                .setUpdatedAt(now)
                                .setCreatedAt(now)
                                .setCashStatus(CashStatus.CAPTURED)
                                .setCashCreatedAt(now)
                                .setCashUpdatedAt(now)
                                .build() : t)
                .collect(Collectors.toList());

        var action = new Action.Builder()
                .setType(RELEASE)
                .setCreatedAt(now)
                .setParentId(releaseOrder.getAction().getId())
                .build();




        var releaseOrderAuthorized = transactions.stream().allMatch(t -> TransactionStatus.CAPTURED.equals(t.getStatus())) ?
                releaseOrder.copy()
                        .setStatus(OrderStatus.RELEASED)
                        .setCreatedAt(now)
                        .setUpdatedAt(now)
                        .setAction(action)
                        .setTransactions(transactions)
                        .build()
                :
                releaseOrder.copy()
                        .setStatus(OrderStatus.AUTHORIZED)
                        .setCreatedAt(now)
                        .setUpdatedAt(now)
                        .setAction(action)
                        .setTransactions(transactions)
                        .build();


        return repository
                .save(releaseOrderAuthorized)
                .cast(SecondaryOrder.class);
    }

	@Override
	public Mono<SecondaryOrder> cancel(Order order, SecondaryOrder secondaryOrder) {
		throw new AmeInvalidInputException("wallet_validation", authorizedMessageException("cancel", RELEASE_ORDER_TYPE));
	}

}
