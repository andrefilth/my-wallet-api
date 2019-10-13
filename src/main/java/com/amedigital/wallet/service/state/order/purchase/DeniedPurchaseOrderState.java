package com.amedigital.wallet.service.state.order.purchase;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.PURCHASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.deniedMessageException;

@Service
public class DeniedPurchaseOrderState implements OrderState<PurchaseOrder> {

    @Override
    public Mono<PurchaseOrder> create(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("criar", PURCHASE_ORDER_TYPE));
    }

    @Override
    public Mono<PurchaseOrder> authorize(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("autorizar", PURCHASE_ORDER_TYPE));
    }

    @Override
    public Mono<PurchaseOrder> capture(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("capturar", PURCHASE_ORDER_TYPE));
    }

    @Override
    public Mono<PurchaseOrder> cancel(PurchaseOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("cancelar", PURCHASE_ORDER_TYPE));
    }
}