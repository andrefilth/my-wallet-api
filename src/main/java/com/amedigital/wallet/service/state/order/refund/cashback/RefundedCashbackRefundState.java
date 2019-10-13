package com.amedigital.wallet.service.state.order.refund.cashback;

import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.model.order.secondary.RefundOrder;
import com.amedigital.wallet.service.state.RefundState;
import reactor.core.publisher.Mono;

public class RefundedCashbackRefundState implements RefundState<CashbackOrder> {

    @Override
    public Mono<RefundOrder> refund(CashbackOrder orderReference, RefundOrder refundOrder) {
        return Mono.error(new AmeInternalException());
    }
}