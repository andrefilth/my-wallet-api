package com.amedigital.wallet.service.state.order.cashout;

import static com.amedigital.wallet.constants.Constants.CASH_OUT_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.deniedMessageException;

import org.springframework.stereotype.Service;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.CashOutOrder;
import com.amedigital.wallet.service.state.OrderState;

import reactor.core.publisher.Mono;

@Service
public class DeniedCashOutOrderState implements OrderState<CashOutOrder> {

    @Override
    public Mono<CashOutOrder> create(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("criar", CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<CashOutOrder> authorize(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("autorizar", CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<CashOutOrder> capture(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("capturar", CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<CashOutOrder> cancel(CashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("cancelar", CASH_OUT_ORDER_TYPE));
    }
}
