package com.amedigital.wallet.service.state.order.cashback.cashin;

import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.order.primary.CashInOrder;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreatedCashInCashbackState  implements SecondaryOrderState<CashInOrder, CashbackOrder> {

    private final OrderRepository repository;

    @Autowired
    public CreatedCashInCashbackState(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<CashbackOrder> create(CashInOrder order, CashbackOrder secondaryOrder) {
        return Mono.error(new AmeInternalException());
    }

    @Override
    public Mono<CashbackOrder> authorize(CashInOrder order, CashbackOrder secondaryOrder) {
        return Mono.error(new AmeInternalException());
    }

    @Override
    public Mono<CashbackOrder> finish(CashInOrder order, CashbackOrder secondaryOrder) {
        return Mono.error(new AmeInternalException());
    }

	@Override
	public Mono<CashbackOrder> cancel(CashInOrder order, CashbackOrder secondaryOrder) {
		 return Mono.error(new AmeInternalException());
	}
}
