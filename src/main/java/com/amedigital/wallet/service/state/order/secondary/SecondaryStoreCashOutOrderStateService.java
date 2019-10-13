package com.amedigital.wallet.service.state.order.secondary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.impl.order.release.ReleaseStoreCashInOrderStateService;
import com.amedigital.wallet.service.impl.order.release.ReleaseStoreCashOutOrderStateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecondaryStoreCashOutOrderStateService implements SecondaryOrderStateService<StoreCashOutOrder, SecondaryOrder> {

    private final Map<OrderType, SecondaryOrderStateService> services = new HashMap<>();

    @Autowired
    public SecondaryStoreCashOutOrderStateService(ReleaseStoreCashOutOrderStateService releaseStoreCashOutOrderStateService) {
        services.put(OrderType.RELEASE, releaseStoreCashOutOrderStateService);
    }

    @Override
    public Mono<SecondaryOrder> create(StoreCashOutOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).create(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> authorize(StoreCashOutOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).authorize(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> finish(StoreCashOutOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).finish(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> cancel(StoreCashOutOrder order, SecondaryOrder secondaryOrder) {
    	return services.get(secondaryOrder.getType()).cancel(order, secondaryOrder);
    }


}
