package com.amedigital.wallet.service.state.order.refund.strategy;

import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.secondary.RefundOrder;
import reactor.core.publisher.Mono;

public interface RefundPaymentType<T> {

    Mono<SecondaryOrder> create(Order purchaseOrder, RefundOrder refundOrder);

    Mono<SecondaryOrder> refund(RefundOrder refundOrder);

    Mono<SecondaryOrder> checkRefundStatus(RefundOrder refundOrder);

}
