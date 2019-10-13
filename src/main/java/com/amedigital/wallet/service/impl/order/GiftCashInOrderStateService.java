package com.amedigital.wallet.service.impl.order;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.model.order.primary.GiftCashInOrder;
import com.amedigital.wallet.service.OrderStateService;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.order.giftcashin.AuthorizedGiftCashInOrderState;
import com.amedigital.wallet.service.state.order.giftcashin.CancelledGiftCashInOrderState;
import com.amedigital.wallet.service.state.order.giftcashin.CapturedGiftCashInOrderState;
import com.amedigital.wallet.service.state.order.giftcashin.CreatedGiftCashInOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class GiftCashInOrderStateService implements OrderStateService<GiftCashInOrder> {

    private final Map<OrderStatus, OrderState> states;

    @Autowired
    public GiftCashInOrderStateService(CreatedGiftCashInOrderState createdGiftCashInOrderState,
                                       AuthorizedGiftCashInOrderState authorizedGiftCashInOrderState,
                                       CancelledGiftCashInOrderState cancelledGiftCashInOrderState,
                                       CapturedGiftCashInOrderState capturedGiftCashInOrderState) {

        Map<OrderStatus, OrderState> statesMap = new EnumMap<>(OrderStatus.class);
        statesMap.put(OrderStatus.CREATED, createdGiftCashInOrderState);
        statesMap.put(OrderStatus.AUTHORIZED, authorizedGiftCashInOrderState);
        statesMap.put(OrderStatus.CANCELLED, cancelledGiftCashInOrderState);
        statesMap.put(OrderStatus.CAPTURED, capturedGiftCashInOrderState);
        states = Collections.unmodifiableMap(statesMap);
    }

    @Override
    public Mono<GiftCashInOrder> create(GiftCashInOrder giftCashInOrder) {
        return states.get(giftCashInOrder.getStatus()).create(giftCashInOrder);
    }

    @Override
    public Mono<GiftCashInOrder> authorize(GiftCashInOrder giftCashInOrder) {
        return states.get(giftCashInOrder.getStatus()).authorize(giftCashInOrder);
    }

    @Override
    public Mono<GiftCashInOrder> capture(GiftCashInOrder giftCashInOrder) {
        return states.get(giftCashInOrder.getStatus()).capture(giftCashInOrder);
    }

    @Override
    public Mono<GiftCashInOrder> cancel(GiftCashInOrder giftCashInOrder) {
        return states.get(giftCashInOrder.getStatus()).cancel(giftCashInOrder);
    }
}
