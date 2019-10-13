package com.amedigital.wallet.repository;

import com.amedigital.wallet.model.order.Action;
import org.jdbi.v3.core.Handle;

public interface ActionRepository {

    Action save(Handle handle, Long orderId, String orderUuid, Action action);

}
