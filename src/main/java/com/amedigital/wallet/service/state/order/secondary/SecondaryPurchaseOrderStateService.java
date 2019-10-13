package com.amedigital.wallet.service.state.order.secondary;


import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.impl.order.cashback.CashbackCashInOrderStateService;
import com.amedigital.wallet.service.impl.order.cashback.CashbackPurchaseOrderStateService;
import com.amedigital.wallet.service.impl.order.release.ReleasePurchaseOrderStateService;
import com.amedigital.wallet.service.state.order.refund.RefundPurchaseOrderStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecondaryPurchaseOrderStateService implements SecondaryOrderStateService<PurchaseOrder, SecondaryOrder> {

    private final Map<OrderType, SecondaryOrderStateService> services = new HashMap<>();


    @Autowired
    public SecondaryPurchaseOrderStateService(ReleasePurchaseOrderStateService releasePurchaseOrderStateService,
                                              RefundPurchaseOrderStateService refundPurchaseOrderStateService,
                                              CashbackPurchaseOrderStateService cashbackPurchaseOrderStateService
                                              ) {

        services.put(OrderType.RELEASE, releasePurchaseOrderStateService);
        services.put(OrderType.REFUND, refundPurchaseOrderStateService);
        services.put(OrderType.CASH_BACK, cashbackPurchaseOrderStateService);

    }

    @Override
    public Mono<SecondaryOrder> create(PurchaseOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).create(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> authorize(PurchaseOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).authorize(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> finish(PurchaseOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).finish(order, secondaryOrder);
    }

	@Override
	public Mono<SecondaryOrder> cancel(PurchaseOrder order, SecondaryOrder secondaryOrder) {
		return services.get(secondaryOrder.getType()).cancel(order, secondaryOrder);
	}
	
}
