package com.amedigital.wallet.service.state.order.cashin;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.CashInOrder;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.cancelledMessageException;

@Service
public class CancelledCashInOrderState implements OrderState<CashInOrder> {

    @Override
    public Mono<CashInOrder> create(CashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("criar", CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<CashInOrder> authorize(CashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("autorizar", CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<CashInOrder> capture(CashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("capturar", CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<CashInOrder> cancel(CashInOrder order) {
        return Mono.just(order);
    }
}
