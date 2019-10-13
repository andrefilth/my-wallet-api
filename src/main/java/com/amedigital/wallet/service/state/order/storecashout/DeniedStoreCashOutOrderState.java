package com.amedigital.wallet.service.state.order.storecashout;

import static com.amedigital.wallet.constants.Constants.STORE_CASH_OUT_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.deniedMessageException;

import org.springframework.stereotype.Service;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
import com.amedigital.wallet.service.state.OrderState;

import reactor.core.publisher.Mono;

@Service
public class DeniedStoreCashOutOrderState implements OrderState<StoreCashOutOrder> {

    @Override
    public Mono<StoreCashOutOrder> create(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("criar", STORE_CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashOutOrder> authorize(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("autorizar", STORE_CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashOutOrder> capture(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("capturar", STORE_CASH_OUT_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashOutOrder> cancel(StoreCashOutOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("cancelar", STORE_CASH_OUT_ORDER_TYPE));
    }
}
