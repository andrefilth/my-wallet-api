package com.amedigital.wallet.repository.mappers;

import com.amedigital.wallet.model.TransactionDataItem;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.amedigital.wallet.util.MapperUtil.toZonedDateTime;

public class TransactionDataItemMapper implements RowMapper<TransactionDataItem> {

    @Override
    public TransactionDataItem map(ResultSet rs, StatementContext ctx) throws SQLException {

        return TransactionDataItem.builder()
                .setAmountInCents(rs.getLong("amount_in_cents"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("created_at").toLocalDateTime()))
                .setOrderId(rs.getString("order_id"))
                .setTransactionId(rs.getString("transaction_id"))
                .build();
    }
}
