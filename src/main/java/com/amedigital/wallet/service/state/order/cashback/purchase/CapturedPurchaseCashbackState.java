package com.amedigital.wallet.service.state.order.cashback.purchase;

import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CapturedPurchaseCashbackState implements SecondaryOrderState<PurchaseOrder, CashbackOrder> {

    private final OrderRepository repository;

    @Autowired
    public CapturedPurchaseCashbackState(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<CashbackOrder> create(PurchaseOrder order, CashbackOrder secondaryOrder) {
        throw new AmeException(400, "release_status_error", "Não é possível CRIAR uma ordem de cashback com status CAPTURED ");

    }

    @Override
    public Mono<CashbackOrder> authorize(PurchaseOrder order, CashbackOrder secondaryOrder) {
        throw new AmeException(400, "release_status_error", "Não é possível AUTORIZAR uma ordem de cashback com status CAPTURED ");

    }

    @Override
    public Mono<CashbackOrder> finish(PurchaseOrder order, CashbackOrder secondaryOrder) {
        return Mono.just(secondaryOrder);
    }

	@Override
	public Mono<CashbackOrder> cancel(PurchaseOrder order, CashbackOrder secondaryOrder) {
        throw new AmeException(400, "release_status_error", "Não é possível CANCELAR uma ordem de cashback com status CAPTURED ");
	}
}
