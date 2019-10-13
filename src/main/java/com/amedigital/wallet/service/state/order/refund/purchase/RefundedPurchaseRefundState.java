package com.amedigital.wallet.service.state.order.refund.purchase;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RefundedPurchaseRefundState implements SecondaryOrderState {


    @Override
    public Mono<SecondaryOrder> create(Order order, SecondaryOrder secondaryOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation", "Você não pode criar um estorno já completado."));
    }

    @Override
    public Mono<SecondaryOrder> authorize(Order order, SecondaryOrder secondaryOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation", "Você não pode autorizar um estorno já completado."));
    }

    @Override
    public Mono<SecondaryOrder> finish(Order order, SecondaryOrder secondaryOrder) {
        return Mono.just(secondaryOrder);
    }

	@Override
	public Mono<SecondaryOrder> cancel(Order order, SecondaryOrder secondaryOrder) {
		return Mono.error(new AmeInvalidInputException("wallet_validation", "Você não pode cancelar um estorno já completado."));
	}

}
