package com.amedigital.wallet.service.state.order.release.cashin;

import static com.amedigital.wallet.constants.Constants.RELEASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.releasedMessageException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.service.state.SecondaryOrderState;

import reactor.core.publisher.Mono;

@Service
public class ReleasedCashInReleaseState implements SecondaryOrderState {

	@Autowired
	public ReleasedCashInReleaseState() {
	}

	@Override
	public Mono<SecondaryOrder> create(Order order, SecondaryOrder secondaryOrder) {
		return Mono.error(new AmeInvalidInputException("wallet_validation",
				releasedMessageException("criar", RELEASE_ORDER_TYPE)));
	}

	@Override
	public Mono<SecondaryOrder> authorize(Order order, SecondaryOrder secondaryOrder) {
		return Mono.error(new AmeInvalidInputException("wallet_validation",
				releasedMessageException("autorizar", RELEASE_ORDER_TYPE)));
	}

	@Override
	public Mono<SecondaryOrder> finish(Order order, SecondaryOrder secondaryOrder) {
		return Mono.just(secondaryOrder);
	}

	@Override
	public Mono<SecondaryOrder> cancel(Order order, SecondaryOrder secondaryOrder) {
		return Mono.error(new AmeInvalidInputException("wallet_validation",
				releasedMessageException("cancelar", RELEASE_ORDER_TYPE)));
	}

}