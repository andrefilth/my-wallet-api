package com.amedigital.wallet.repository.mappers.query;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.endoint.response.v3.query.SimpleReleaseStatementResponse;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.amedigital.wallet.util.MapperUtil.toZonedDateTime;

public class SimpleReleaseStatementMapper implements RowMapper<SimpleReleaseStatementResponse> {

    @Override
    public SimpleReleaseStatementResponse map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new SimpleReleaseStatementResponse(
                    rs.getString("order_uuid"),
                    OrderType.valueOf(rs.getString("order_type")),
                    OrderStatus.valueOf(rs.getString("order_status")),
                    rs.getString("order_reference_order_uuid"),
                    rs.getString("order_secondary_id"),
                    rs.getString("transaction_uuid"),
                    toZonedDateTime(rs.getTimestamp("transaction_release_date").toLocalDateTime())
                    );

            }

}
