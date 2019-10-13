package com.amedigital.wallet.endoint.request.order;

import com.amedigital.wallet.constants.enuns.OrderType;

public class RefundOrderRequest extends OrderRequest {

    public RefundOrderRequest() {
        super(OrderType.REFUND);
    }

}
