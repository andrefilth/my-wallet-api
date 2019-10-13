package com.amedigital.wallet.service.impl.order.cashback;


import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.order.primary.CashInOrder;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.cashback.cashin.AuthorizedCashInCashbackState;
import com.amedigital.wallet.service.state.order.cashback.cashin.CapturedCashInCashbackState;
import com.amedigital.wallet.service.state.order.cashback.cashin.CreatedCashInCashbackState;
import com.amedigital.wallet.service.state.order.release.cashin.CreatedCashInReleaseState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class CashbackCashInOrderStateService implements SecondaryOrderStateService<CashInOrder, CashbackOrder> {


    private final Map<OrderStatus, SecondaryOrderState<CashInOrder, CashbackOrder>> states = new HashMap<>();
    private final WalletRepository walletRepository;

    @Autowired
    public CashbackCashInOrderStateService(WalletRepository walletRepository,
                                           CreatedCashInCashbackState createdCashInCashbackState,
                                           AuthorizedCashInCashbackState authorizedCashInCashbackState,
                                           CapturedCashInCashbackState capturedCashInCashbackState
    ) {
        this.walletRepository = walletRepository;

        states.put(OrderStatus.CREATED, createdCashInCashbackState);
        states.put(OrderStatus.AUTHORIZED, authorizedCashInCashbackState);
        states.put(OrderStatus.RELEASED, capturedCashInCashbackState);

    }

    @Override
    public Mono<CashbackOrder> create(CashInOrder cashInOrder, CashbackOrder cashbackOrder) {
        return Mono.error(new AmeInternalException());
    }

    @Override
    public Mono<CashbackOrder> authorize(CashInOrder cashInOrder, CashbackOrder cashbackOrder) {
        return Mono.error(new AmeInternalException());
    }

    @Override
    public Mono<CashbackOrder> finish(CashInOrder cashInOrder, CashbackOrder cashbackOrder) {
        return Mono.error(new AmeInternalException());
    }

	@Override
	public Mono<CashbackOrder> cancel(CashInOrder t, CashbackOrder k) {
		return Mono.error(new AmeInternalException());
	}
}
