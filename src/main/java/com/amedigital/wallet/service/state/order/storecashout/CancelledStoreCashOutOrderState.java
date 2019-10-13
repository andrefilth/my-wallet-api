package com.amedigital.wallet.service.state.order.storecashout;

import static com.amedigital.wallet.constants.Constants.STORE_CASH_OUT_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.cancelledMessageException;

import org.springframework.stereotype.Service;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
import com.amedigital.wallet.service.state.OrderState;

import reactor.core.publisher.Mono;

@Service
public class CancelledStoreCashOutOrderState implements OrderState<StoreCashOutOrder> {

    @Override
    public Mono<StoreCashOutOrder> create(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("criar", STORE_CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashOutOrder> authorize(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("autorizar", STORE_CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashOutOrder> capture(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("capturar", STORE_CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashOutOrder> cancel(StoreCashOutOrder order) {
        return Mono.just(order);
    }

}