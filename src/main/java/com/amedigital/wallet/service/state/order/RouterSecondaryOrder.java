package com.amedigital.wallet.service.state.order;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.service.OrderService;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.secondary.SecondaryCashInOrderStateService;
import com.amedigital.wallet.service.state.order.secondary.SecondaryPurchaseOrderStateService;
import com.amedigital.wallet.service.state.order.secondary.SecondaryStoreCashInOrderStateService;
import com.amedigital.wallet.service.state.order.secondary.SecondaryStoreCashOutOrderStateService;
import com.amedigital.wallet.service.state.order.secondary.SecondaryTransferBetweenWalletsOrderStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class RouterSecondaryOrder implements SecondaryOrderState {


    private static final Logger LOG = LoggerFactory.getLogger(RouterSecondaryOrder.class);

    private final Map<OrderType, SecondaryOrderStateService> services = new HashMap<>();
    private final OrderService orderService;

    @Autowired
    public RouterSecondaryOrder(SecondaryPurchaseOrderStateService secondaryPurchaseOrderStateService,
                                SecondaryCashInOrderStateService secondaryCashInOrderStateService,
                                SecondaryTransferBetweenWalletsOrderStateService secondaryTransferBetweenWalletsOrderStateService,
                                SecondaryStoreCashInOrderStateService secondaryStoreCashInOrderStateService,
                                SecondaryStoreCashOutOrderStateService secondaryStoreCashOutOrderStateService,
                                OrderService orderService) {

        services.put(OrderType.CASH_IN, secondaryCashInOrderStateService);
        services.put(OrderType.PURCHASE, secondaryPurchaseOrderStateService);
        services.put(OrderType.TRANSFER_BETWEEN_WALLETS, secondaryTransferBetweenWalletsOrderStateService);
        services.put(OrderType.STORE_CASH_IN, secondaryStoreCashInOrderStateService);
        services.put(OrderType.STORE_CASH_OUT, secondaryStoreCashOutOrderStateService);

        this.orderService = orderService;
    }

    @Override
    public Mono<SecondaryOrder> create(Order order, SecondaryOrder secondaryOrder) {

        if (secondaryOrder.getType().equals(OrderType.RELEASE)) {

            return orderService.findReleaseOrderByPrimaryOrder(order.getUuid())
                    .flatMap($ -> {
                        LOG.error("Release order duplicada para a order: [{}]", order.getUuid());
                        return Mono.error(new AmeException(400, "duplicate_release_order", "Já existe um release para a order"));
                    })
                    .then(services.get(order.getType()).create(order, secondaryOrder));
        } else {
            return services.get(order.getType()).create(order, secondaryOrder);
        }
    }

    @Override
    public Mono<SecondaryOrder> authorize(Order order, SecondaryOrder secondaryOrder) {
        return services.get(order.getType()).authorize(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> finish(Order order, SecondaryOrder secondaryOrder) {
        return services.get(order.getType()).finish(order, secondaryOrder);
    }
    @Override
    public Mono<SecondaryOrder> cancel(Order order, SecondaryOrder secondaryOrder) {
    	return services.get(order.getType()).cancel(order, secondaryOrder);
    }

    public Mono<SecondaryOrder> changeStatus(Order order, SecondaryOrder secondaryOrder, OrderStatus orderStatus) {
        switch (orderStatus) {
            case CREATED:
                return create(order, secondaryOrder);
            case AUTHORIZED:
                return authorize(order, secondaryOrder);
            case CAPTURED:
                return finish(order, secondaryOrder);
            case RELEASED:
                return finish(order, secondaryOrder);
            case REFUNDED:
                if (OrderStatus.CREATED.equals(secondaryOrder.getStatus())) {
                    return create(order, secondaryOrder);
                }

                if (OrderStatus.PENDING.equals(secondaryOrder.getStatus()) || OrderStatus.AUTHORIZED.equals(secondaryOrder.getStatus())) {
                    return finish(order, secondaryOrder);
                }

                return Mono.just(secondaryOrder);
            case CANCELLED:
            	return cancel(order, secondaryOrder);
            default:
                return Mono.error(new AmeInvalidInputException("unsupported_order_status", "O status da ordem não é suportado"));
        }

    }

}