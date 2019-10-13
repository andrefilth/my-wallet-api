package com.amedigital.wallet.service.state.order.cashout;

import static com.amedigital.wallet.constants.Constants.CASH_OUT_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.capturedMessageException;

import org.springframework.stereotype.Service;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.CashOutOrder;
import com.amedigital.wallet.service.state.OrderState;

import reactor.core.publisher.Mono;

@Service
public class CapturedCashOutOrderState implements OrderState<CashOutOrder> {

    @Override
    public Mono<CashOutOrder> create(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("criar", CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<CashOutOrder> authorize(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("autorizar", CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<CashOutOrder> capture(CashOutOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<CashOutOrder> cancel(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("cancelar", CASH_OUT_ORDER_TYPE));
    }
}
