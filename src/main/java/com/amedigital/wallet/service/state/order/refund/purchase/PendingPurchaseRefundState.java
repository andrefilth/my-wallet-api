package com.amedigital.wallet.service.state.order.refund.purchase;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.secondary.RefundOrder;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.refund.strategy.CashRefund;
import com.amedigital.wallet.service.state.order.refund.strategy.CreditCardRefund;
import com.amedigital.wallet.service.state.order.refund.strategy.MultipleRefund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PendingPurchaseRefundState implements SecondaryOrderState {

    private final CreditCardRefund creditCardRefund;
    private final CashRefund cashRefund;
    private final MultipleRefund multipleRefund;

    @Autowired
    public PendingPurchaseRefundState(CreditCardRefund creditCardRefund, CashRefund cashRefund, MultipleRefund multipleRefund) {
        this.creditCardRefund = creditCardRefund;
        this.cashRefund = cashRefund;
        this.multipleRefund = multipleRefund;
    }

    @Override
    public Mono<SecondaryOrder> create(Order purchaseOrder, SecondaryOrder refundOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation", "Você não pode criar um estorno já em andamento."));
    }

    @Override
    public Mono<SecondaryOrder> authorize(Order purchaseOrder, SecondaryOrder refundOrder) {
        return Mono.just(refundOrder);
    }

    @Override
    public Mono<SecondaryOrder> finish(Order purchaseOrder, SecondaryOrder secondaryOrder) {
        var refundOrder = (RefundOrder) secondaryOrder;

        var isCreditCardPayment = purchaseOrder.getTransactions()
                .stream()
                .allMatch(t -> PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod()));

        var isCashPayment = purchaseOrder.getTransactions()
                .stream()
                .allMatch(t -> PaymentMethod.CASH.equals(t.getPaymentMethod()));

        //TODO: REFATORAR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (isCreditCardPayment) {
            return creditCardRefund.checkRefundStatus(refundOrder);

        } else if (isCashPayment) {
            return cashRefund.checkRefundStatus(refundOrder);

        } else {
            return multipleRefund.checkRefundStatus(refundOrder);
        }
    }

	@Override
	public Mono<SecondaryOrder> cancel(Order order, SecondaryOrder secondaryOrder) {
		 return Mono.error(new AmeInvalidInputException("wallet_validation", "Você não pode criar um estorno já em andamento."));
	}

}
