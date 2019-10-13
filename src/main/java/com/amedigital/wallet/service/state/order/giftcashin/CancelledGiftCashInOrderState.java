package com.amedigital.wallet.service.state.order.giftcashin;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.GiftCashInOrder;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.GIFT_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.cancelledMessageException;

@Service
public class CancelledGiftCashInOrderState implements OrderState<GiftCashInOrder> {
    @Override
    public Mono<GiftCashInOrder> create(GiftCashInOrder order) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("criar", GIFT_CASH_IN_ORDER_TYPE)));
    }

    @Override
    public Mono<GiftCashInOrder> authorize(GiftCashInOrder order) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("autorizar", GIFT_CASH_IN_ORDER_TYPE)));
    }

    @Override
    public Mono<GiftCashInOrder> capture(GiftCashInOrder order) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("capturar", GIFT_CASH_IN_ORDER_TYPE)));
    }

    @Override
    public Mono<GiftCashInOrder> cancel(GiftCashInOrder order) {
        return Mono.just(order);
    }
}
