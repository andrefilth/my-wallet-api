package com.amedigital.wallet.service.state.order.refund;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.order.secondary.RefundOrder;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.refund.purchase.CreatedPurchaseRefundState;
import com.amedigital.wallet.service.state.order.refund.purchase.PendingPurchaseRefundState;
import com.amedigital.wallet.service.state.order.refund.purchase.RefundedPurchaseRefundState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class RefundPurchaseOrderStateService implements SecondaryOrderStateService<PurchaseOrder, RefundOrder> {

    private final Map<OrderStatus, SecondaryOrderState> states = new HashMap<>();

    @Autowired
    public RefundPurchaseOrderStateService(CreatedPurchaseRefundState createdPurchaseRefundState,
                                           PendingPurchaseRefundState pendingPurchaseRefundState,
                                           RefundedPurchaseRefundState refundedPurchaseRefundState) {

        states.put(OrderStatus.CREATED, createdPurchaseRefundState);
        states.put(OrderStatus.AUTHORIZED, pendingPurchaseRefundState);
        states.put(OrderStatus.PENDING, pendingPurchaseRefundState);
        states.put(OrderStatus.REFUNDED, refundedPurchaseRefundState);

    }

    @Override
    public Mono<RefundOrder> create(PurchaseOrder purchaseOrder, RefundOrder refundOrder) {
        return states.get(refundOrder.getStatus()).create(purchaseOrder, refundOrder);
    }

    @Override
    public Mono<RefundOrder> authorize(PurchaseOrder purchaseOrder, RefundOrder refundOrder) {
        return states.get(refundOrder.getStatus()).authorize(purchaseOrder, refundOrder);
    }

    @Override
    public Mono<RefundOrder> finish(PurchaseOrder purchaseOrder, RefundOrder refundOrder) {
        return states.get(refundOrder.getStatus()).finish(purchaseOrder, refundOrder);
    }

	@Override
	public Mono<RefundOrder> cancel(PurchaseOrder purchaseOrder, RefundOrder refundOrder) {
		return states.get(refundOrder.getStatus()).cancel(purchaseOrder, refundOrder);
	}
}
