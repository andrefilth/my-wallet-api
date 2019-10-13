package com.amedigital.wallet.service.state.order.cashin;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.capturedMessageException;

@Service
public class CapturedCashInOrderState implements OrderState<Order> {

    @Override
    public Mono<Order> create(Order order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("criar", CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<Order> authorize(Order order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("autorizar", CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<Order> capture(Order order) {
        return Mono.just(order);
    }

    @Override
    public Mono<Order> cancel(Order order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("cancelar", CASH_IN_ORDER_TYPE));
    }
}
