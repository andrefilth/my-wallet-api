package com.amedigital.wallet.endoint.request.order;

import static com.amedigital.wallet.constants.enuns.OrderType.CASH_BACK;

public class CashBackOrderRequest extends OrderRequest {

    public CashBackOrderRequest() {
        super(CASH_BACK);
    }

}
