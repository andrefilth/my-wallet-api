package com.amedigital.wallet.service.state.order.cashback.transferbetweenwallets;

import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthorizedTransferBetweenWalletsCashbackState
		implements SecondaryOrderState<TransferBetweenWalletsOrder, CashbackOrder> {

	private final OrderRepository repository;

	@Autowired
	public AuthorizedTransferBetweenWalletsCashbackState(OrderRepository repository) {
		this.repository = repository;
	}

	@Override
	public Mono<CashbackOrder> create(TransferBetweenWalletsOrder order, CashbackOrder secondaryOrder) {
		return Mono.error(new AmeInternalException());
	}

	@Override
	public Mono<CashbackOrder> authorize(TransferBetweenWalletsOrder order, CashbackOrder secondaryOrder) {
		return Mono.error(new AmeInternalException());
	}

	@Override
	public Mono<CashbackOrder> finish(TransferBetweenWalletsOrder order, CashbackOrder secondaryOrder) {
		return Mono.error(new AmeInternalException());
	}

	@Override
	public Mono<CashbackOrder> cancel(TransferBetweenWalletsOrder order, CashbackOrder secondaryOrder) {
		return Mono.error(new AmeInternalException());
	}
}
