package com.amedigital.wallet.endoint.converter.order.secondary;

import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.request.order.OrderRequest;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;

public interface SecondaryOrderRequestConverter<T extends OrderRequest, R extends SecondaryOrder> {

    R from(Order order, T orderRequest, RequestContext requestContext);
}