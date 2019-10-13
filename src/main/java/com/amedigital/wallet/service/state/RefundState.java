package com.amedigital.wallet.service.state;

import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.secondary.RefundOrder;
import reactor.core.publisher.Mono;

public interface RefundState<T extends Order> {

    Mono<RefundOrder> refund(T orderReference, RefundOrder refundOrder);
}
