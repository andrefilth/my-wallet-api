package com.amedigital.wallet.service.state.order.cashback.purchase;

import org.springframework.beans.factory.annotation.Autowired;

import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.SecondaryOrderState;

import reactor.core.publisher.Mono;

public class CancelledPurchaseCashbackState implements SecondaryOrderState<PurchaseOrder, CashbackOrder> {

	private final OrderRepository repository;
	private final PaymentMethodRouter router;

	@Autowired
	public CancelledPurchaseCashbackState(OrderRepository repository, PaymentMethodRouter router) {
		this.repository = repository;
		this.router = router;
	}

	@Override
	public Mono<CashbackOrder> create(PurchaseOrder order, CashbackOrder secondaryOrder) {
		throw new AmeException(400, "release_status_error",
				"Não é possível CRIAR uma ordem de cashback com status CANCELLED ");
	}

	@Override
	public Mono<CashbackOrder> authorize(PurchaseOrder order, CashbackOrder secondaryOrder) {
		throw new AmeException(400, "release_status_error",
				"Não é possível AUTORIZAR uma ordem de cashback com status CANCELLED ");
	}

	@Override
	public Mono<CashbackOrder> finish(PurchaseOrder order, CashbackOrder secondaryOrder) {
		throw new AmeException(400, "release_status_error",
				"Não é possível FINALIZAR uma ordem de cashback com status CANCELLED ");
	}

	@Override
	public Mono<CashbackOrder> cancel(PurchaseOrder order, CashbackOrder secondaryOrder) {
		// TODO Auto-generated method stub
		return null;
	}

}
