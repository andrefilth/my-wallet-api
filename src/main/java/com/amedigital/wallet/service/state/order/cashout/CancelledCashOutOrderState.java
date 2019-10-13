package com.amedigital.wallet.service.state.order.cashout;

import static com.amedigital.wallet.constants.Constants.CASH_OUT_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.cancelledMessageException;

import org.springframework.stereotype.Service;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.CashOutOrder;
import com.amedigital.wallet.service.state.OrderState;

import reactor.core.publisher.Mono;

@Service
public class CancelledCashOutOrderState implements OrderState<CashOutOrder> {

    @Override
    public Mono<CashOutOrder> create(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("criar", CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<CashOutOrder> authorize(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("autorizar", CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<CashOutOrder> capture(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("capturar", CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<CashOutOrder> cancel(CashOutOrder order) {
        return Mono.just(order);
    }

}