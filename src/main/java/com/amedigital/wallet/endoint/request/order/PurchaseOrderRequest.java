package com.amedigital.wallet.endoint.request.order;

import static com.amedigital.wallet.constants.enuns.OrderType.PURCHASE;

public class PurchaseOrderRequest extends OrderRequest {

    public PurchaseOrderRequest() {
        super(PURCHASE);
    }

}
