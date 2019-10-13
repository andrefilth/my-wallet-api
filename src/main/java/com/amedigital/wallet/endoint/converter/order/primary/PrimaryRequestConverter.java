package com.amedigital.wallet.endoint.converter.order.primary;

import com.amedigital.wallet.endoint.converter.RequestContext;

public interface PrimaryRequestConverter<T, R> {


    R from(T t, RequestContext context);
}
