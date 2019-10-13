package com.amedigital.wallet.service.state.order.refund.cashback;

import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import reactor.core.publisher.Mono;

public class PendingCashbackRefundState implements SecondaryOrderState {

    @Override
    public Mono<SecondaryOrder> create(Order order, SecondaryOrder secondaryOrder) {
        return Mono.error(new AmeInternalException());
    }

    @Override
    public Mono<SecondaryOrder> authorize(Order order, SecondaryOrder secondaryOrder) {
        return Mono.error(new AmeInternalException());
    }

    @Override
    public Mono<SecondaryOrder> finish(Order order, SecondaryOrder secondaryOrder) {
        return Mono.error(new AmeInternalException());
    }

	@Override
	public Mono<SecondaryOrder>  cancel(Order order, SecondaryOrder secondaryOrder) {
		return Mono.error(new AmeInternalException());
	}


}