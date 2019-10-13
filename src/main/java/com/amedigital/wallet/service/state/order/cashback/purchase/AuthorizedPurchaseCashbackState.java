package com.amedigital.wallet.service.state.order.cashback.purchase;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;


@Service
public class AuthorizedPurchaseCashbackState implements SecondaryOrderState<PurchaseOrder, CashbackOrder> {

    private final OrderRepository repository;
    private final PaymentMethodRouter router;

    @Autowired
    public AuthorizedPurchaseCashbackState(OrderRepository repository, PaymentMethodRouter router) {
        this.repository = repository;
        this.router = router;
    }

    @Override
    public Mono<CashbackOrder> create(PurchaseOrder order, CashbackOrder secondaryOrder) {
        throw new AmeException(400, "release_status_error", "Não é possível CRIAR uma ordem de cashback com status AUTHORIZED ");
    }

    @Override
    public Mono<CashbackOrder> authorize(PurchaseOrder order, CashbackOrder secondaryOrder) {
        return Mono.just(secondaryOrder);
    }

    @Override
    public Mono<CashbackOrder> finish(PurchaseOrder order, CashbackOrder secondaryOrder) {
        return Flux.fromStream(secondaryOrder.getTransactions().stream())
                .flatMap(router::capture)
                .collectList()
                .map(trs -> {
                    var status = (trs.stream().allMatch(e -> e.getStatus().equals(TransactionStatus.CAPTURED))) ? OrderStatus.CAPTURED : OrderStatus.AUTHORIZED;
                    var newOrder = new Action.Builder().setParentId(order.getAction().getId()).setType(ActionType.CAPTURE).setCreatedAt(ZonedDateTime.now()).build();
                    return (Order) secondaryOrder.copy()
                            .setStatus(status)
                            .setTransactions(trs)
                            .setAction(newOrder)
                            .setCreatedAt(secondaryOrder.getCreatedAt())
                            .build();
                }).flatMap(repository::save).cast(CashbackOrder.class);

    }

	@Override
	public Mono<CashbackOrder> cancel(PurchaseOrder order, CashbackOrder secondaryOrder) {
		 return Flux.fromStream(secondaryOrder.getTransactions().stream())
				    .flatMap(router::cancel)
				    .collectList()
				    .map(transaction ->{
				    	var newOrder = new Action.Builder().setParentId(order.getAction().getId()).setType(ActionType.CANCEL).setCreatedAt(ZonedDateTime.now()).build();
				    	return (Order) secondaryOrder.copy()
	                            .setStatus(OrderStatus.CANCELLED)
	                            .setTransactions(transaction)
	                            .setAction(newOrder)
	                            .setCreatedAt(secondaryOrder.getCreatedAt())
	                            .build();
				    })
				    .flatMap(repository::save).cast(CashbackOrder.class);
				    
	}
}
