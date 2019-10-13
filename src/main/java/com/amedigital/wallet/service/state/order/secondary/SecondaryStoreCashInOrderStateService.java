package com.amedigital.wallet.service.state.order.secondary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.primary.StoreCashInOrder;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.impl.order.release.ReleaseStoreCashInOrderStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecondaryStoreCashInOrderStateService implements SecondaryOrderStateService<StoreCashInOrder, SecondaryOrder> {

    private final Map<OrderType, SecondaryOrderStateService> services = new HashMap<>();

    @Autowired
    public SecondaryStoreCashInOrderStateService(ReleaseStoreCashInOrderStateService releaseStoreCashInOrderStateService) {
        services.put(OrderType.RELEASE, releaseStoreCashInOrderStateService);
    }

    @Override
    public Mono<SecondaryOrder> create(StoreCashInOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).create(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> authorize(StoreCashInOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).authorize(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> finish(StoreCashInOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).finish(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> cancel(StoreCashInOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).cancel(order, secondaryOrder);
    }


}
