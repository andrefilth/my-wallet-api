package com.amedigital.wallet.service.state.order.storecashin;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.StoreCashInOrder;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.STORE_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.cancelledMessageException;

@Service
public class CancelledStoreCashInOrderState implements OrderState<StoreCashInOrder> {

    @Override
    public Mono<StoreCashInOrder> create(StoreCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("criar", STORE_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashInOrder> authorize(StoreCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("autorizar", STORE_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashInOrder> capture(StoreCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("capturar", STORE_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<StoreCashInOrder> cancel(StoreCashInOrder order) {
        return Mono.just(order);
    }

}