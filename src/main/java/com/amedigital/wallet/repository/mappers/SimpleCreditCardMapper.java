package com.amedigital.wallet.repository.mappers;

import com.amedigital.wallet.endoint.response.SimpleCreditCardResponse;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SimpleCreditCardMapper implements RowMapper<SimpleCreditCardResponse> {

    @Override
    public SimpleCreditCardResponse map(ResultSet rs, StatementContext ctx) throws SQLException {
        SimpleCreditCardResponse response = new SimpleCreditCardResponse();
        response.setCardId(rs.getString("credit_card_id"));
        response.setWalletId(rs.getString("wallet_uuid"));
        response.setNumberOfInstallments(rs.getInt("number_of_installments"));

        return response;
    }
}
