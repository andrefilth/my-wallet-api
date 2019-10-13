package com.amedigital.wallet.service.state.order.purchase;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.PURCHASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.cancelledMessageException;

@Service
public class CancelledPurchaseOrderState implements OrderState<PurchaseOrder> {

    @Override
    public Mono<PurchaseOrder> create(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("criar", PURCHASE_ORDER_TYPE));
    }

    @Override
    public Mono<PurchaseOrder> authorize(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("autorizar", PURCHASE_ORDER_TYPE));
    }

    @Override
    public Mono<PurchaseOrder> capture(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("capturar", PURCHASE_ORDER_TYPE));
    }

    @Override
    public Mono<PurchaseOrder> cancel(PurchaseOrder order) {
        return Mono.just(order);
    }
}