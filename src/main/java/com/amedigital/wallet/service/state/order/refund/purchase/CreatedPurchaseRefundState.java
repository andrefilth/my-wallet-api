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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreatedPurchaseRefundState implements SecondaryOrderState {

    private static final Logger LOG = LoggerFactory.getLogger(CreatedPurchaseRefundState.class);

    private final CreditCardRefund creditCardRefund;
    private final CashRefund cashRefund;
    private final MultipleRefund multipleRefund;

    @Autowired
    public CreatedPurchaseRefundState(CreditCardRefund creditCardRefund, CashRefund cashRefund, MultipleRefund multipleRefund) {
        this.creditCardRefund = creditCardRefund;
        this.cashRefund = cashRefund;
        this.multipleRefund = multipleRefund;
    }

    @Override
    public Mono<SecondaryOrder> create(Order purchaseOrder, SecondaryOrder secondaryOrder) {
        var refundOrder = (RefundOrder) secondaryOrder;

        var isCreditCardPayment = purchaseOrder.getTransactions()
                .stream()
                .allMatch(t -> PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod()));

        var isCashPayment = purchaseOrder.getTransactions()
                .stream()
                .allMatch(t -> PaymentMethod.CASH.equals(t.getPaymentMethod()));

        //TODO: REFATORAR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (isCreditCardPayment) {
            return creditCardRefund.create(purchaseOrder, refundOrder);

        } else if (isCashPayment) {
            return cashRefund.create(purchaseOrder, refundOrder);

        } else {
            return multipleRefund.create(purchaseOrder, refundOrder);
        }
    }

    @Override
    public Mono<SecondaryOrder> authorize(Order purchaseOrder, SecondaryOrder secondaryOrder) {
        var refundOrder = (RefundOrder) secondaryOrder;

        var isCreditCardPayment = purchaseOrder.getTransactions()
                .stream()
                .allMatch(t -> PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod()));

        var isCashPayment = purchaseOrder.getTransactions()
                .stream()
                .allMatch(t -> PaymentMethod.CASH.equals(t.getPaymentMethod()));

        //TODO: REFATORAR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (isCreditCardPayment) {
            return creditCardRefund.refund(refundOrder);

        } else if (isCashPayment) {
            return cashRefund.refund(refundOrder);

        } else {
            return multipleRefund.refund(refundOrder);
        }
    }

    @Override
    public Mono<SecondaryOrder> finish(Order purchaseOrder, SecondaryOrder refundOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation", "Você não pode finalizar o estorno de uma ordem criada."));
    }

	@Override
	public Mono<SecondaryOrder> cancel(Order order, SecondaryOrder secondaryOrder) {
		return Mono.error(new AmeInvalidInputException("wallet_validation", "Você não pode cancelar o estorno de uma ordem criada."));
	}

}