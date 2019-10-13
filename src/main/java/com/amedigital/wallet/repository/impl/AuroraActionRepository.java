package com.amedigital.wallet.repository.impl;

import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.repository.ActionRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.locator.ClasspathSqlLocator;
import org.springframework.stereotype.Repository;

@Repository
public class AuroraActionRepository implements ActionRepository {

    @Override
    public Action save(Handle handle, Long orderId, String orderUuid, Action action) {
        String sql = ClasspathSqlLocator.findSqlOnClasspath("sql.action.action-insert");

        return handle.createUpdate(sql)
                .bind("parent_action_id", action.getParentId())
                .bind("type", action.getType())
                .bind("order_id", orderId)
                .bind("order_uuid", orderUuid)
                .bind("created_at", action.getCreatedAt())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(Long.class)
                .findFirst()
                .map(actionId -> action.copy()
                        .setId(actionId)
                        .build())
                .orElse(null);
    }
}
