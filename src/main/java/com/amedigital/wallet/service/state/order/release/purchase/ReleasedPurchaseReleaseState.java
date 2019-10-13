package com.amedigital.wallet.service.state.order.release.purchase;

import static com.amedigital.wallet.constants.Constants.RELEASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.releasedMessageException;

import org.springframework.stereotype.Service;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.service.state.SecondaryOrderState;

import reactor.core.publisher.Mono;

@Service
public class ReleasedPurchaseReleaseState implements SecondaryOrderState {

	private static final String VALIDATION = "wallet_validation";

	@Override
	public Mono<SecondaryOrder> create(Order order, SecondaryOrder releaseOrder) {
		throw new AmeInvalidInputException(VALIDATION, releasedMessageException("criar", RELEASE_ORDER_TYPE));
	}

	@Override
	public Mono<SecondaryOrder> authorize(Order order, SecondaryOrder releaseOrder) {
		throw new AmeInvalidInputException(VALIDATION, releasedMessageException("autorizar", RELEASE_ORDER_TYPE));
	}

	@Override
	public Mono<SecondaryOrder> finish(Order order, SecondaryOrder releaseOrder) {
		return Mono.just(releaseOrder);
	}

	@Override
	public Mono<SecondaryOrder> cancel(Order order, SecondaryOrder secondaryOrder) {
		throw new AmeInvalidInputException(VALIDATION, releasedMessageException("cancelar", RELEASE_ORDER_TYPE));
	}

}