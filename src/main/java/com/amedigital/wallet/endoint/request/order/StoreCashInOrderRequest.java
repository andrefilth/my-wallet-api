package com.amedigital.wallet.endoint.request.order;

import static com.amedigital.wallet.constants.enuns.OrderType.STORE_CASH_IN;

public class StoreCashInOrderRequest extends OrderRequest {

    public StoreCashInOrderRequest() {
        super(STORE_CASH_IN);
    }

}
