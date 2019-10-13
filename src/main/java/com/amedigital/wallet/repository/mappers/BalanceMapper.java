package com.amedigital.wallet.repository.mappers;

import com.amedigital.wallet.model.Balance;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BalanceMapper implements RowMapper<Balance> {

    @Override
    public Balance map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Balance.builder()
                .setAvailable(rs.getLong("available_amount"))
                .setFutureCredit(rs.getLong("future_credit"))
                .setFutureDebit(rs.getLong("future_debit"))
                .build();

    }
}
