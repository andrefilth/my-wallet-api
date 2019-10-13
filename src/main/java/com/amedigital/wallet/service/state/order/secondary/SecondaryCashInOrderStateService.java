package com.amedigital.wallet.service.state.order.secondary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.primary.CashInOrder;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.impl.order.cashback.CashbackCashInOrderStateService;
import com.amedigital.wallet.service.impl.order.release.ReleaseCashInOrderStateService;
import com.amedigital.wallet.service.impl.order.release.ReleaseStoreCashInOrderStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


@Service
public class SecondaryCashInOrderStateService implements SecondaryOrderStateService<CashInOrder, SecondaryOrder> {

    private final Map<OrderType, SecondaryOrderStateService> services = new HashMap<>();

    @Autowired
    public SecondaryCashInOrderStateService(
            ReleaseCashInOrderStateService releaseCashInOrderStateService,
            CashbackCashInOrderStateService cashbackCashInOrderStateService,
            ReleaseStoreCashInOrderStateService releaseStoreCashInOrderStateService) {
        services.put(OrderType.RELEASE, releaseCashInOrderStateService);
        services.put(OrderType.CASH_BACK, cashbackCashInOrderStateService);
        services.put(OrderType.STORE_CASH_IN, releaseStoreCashInOrderStateService);
    }

    @Override
    public Mono<SecondaryOrder> create(CashInOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).create(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> authorize(CashInOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).authorize(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> finish(CashInOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).finish(order, secondaryOrder);
    }

	@Override
	public Mono<SecondaryOrder> cancel(CashInOrder order, SecondaryOrder secondaryOrder) {
		return services.get(secondaryOrder.getType()).cancel(order, secondaryOrder);
	}

}
