package com.amedigital.wallet.endoint.request.order;

import static com.amedigital.wallet.constants.enuns.OrderType.STORE_CASH_OUT;

public class StoreCashOutOrderRequest extends OrderRequest {

    public StoreCashOutOrderRequest() {
        super(STORE_CASH_OUT);
    }
}
