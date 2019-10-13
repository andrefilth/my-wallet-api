package com.amedigital.wallet.endoint.request.order;

import com.amedigital.wallet.constants.enuns.OrderType;

public class CashInOrderRequest extends OrderRequest {

    public CashInOrderRequest() {
        super(OrderType.CASH_IN);
    }

}
