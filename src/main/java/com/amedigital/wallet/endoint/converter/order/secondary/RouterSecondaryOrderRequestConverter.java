package com.amedigital.wallet.endoint.converter.order.secondary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.request.order.CashBackOrderRequest;
import com.amedigital.wallet.endoint.request.order.OrderRequest;
import com.amedigital.wallet.endoint.request.order.RefundOrderRequest;
import com.amedigital.wallet.endoint.request.order.ReleaseOrderRequest;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class RouterSecondaryOrderRequestConverter implements SecondaryOrderRequestConverter<OrderRequest, SecondaryOrder> {

    private final RefundRequestConverter refundRequestConverter;
    private final ReleaseRequestConverter releaseRequestConverter;
    private final CashBackRequestConverter cashBackRequestConverter;

    @Autowired
    public RouterSecondaryOrderRequestConverter(RefundRequestConverter refundRequestConverter, ReleaseRequestConverter releaseRequestConverter, CashBackRequestConverter cashBackRequestConverter) {
        this.refundRequestConverter = refundRequestConverter;
        this.releaseRequestConverter = releaseRequestConverter;
        this.cashBackRequestConverter = cashBackRequestConverter;
    }

    @Override
    public SecondaryOrder from(Order primaryOrder, OrderRequest orderRequest, RequestContext requestContext) {
        OrderType type = orderRequest.getType();
        switch (type) {
            case REFUND: return refundRequestConverter.from(primaryOrder, (RefundOrderRequest) orderRequest, requestContext);
            case RELEASE: return releaseRequestConverter.from(primaryOrder, (ReleaseOrderRequest) orderRequest, requestContext);
            case CASH_BACK: return cashBackRequestConverter.from(primaryOrder, (CashBackOrderRequest) orderRequest, requestContext);
            default:
                throw new AmeInternalException("O tipo de ordem não é suportada.");
        }
    }
}
