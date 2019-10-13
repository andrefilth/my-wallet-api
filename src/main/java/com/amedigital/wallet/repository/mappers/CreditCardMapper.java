package com.amedigital.wallet.repository.mappers;

import com.amedigital.wallet.model.CreditCard;
import com.amedigital.wallet.service.atom.response.enums.CardBrand;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CreditCardMapper implements RowMapper<CreditCard> {
    @Override
    public CreditCard map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new CreditCard.Builder()
                .setId(rs.getLong("id"))
                .setToken(rs.getString("token"))
                .setHash(rs.getString("hash"))
                .setHolder(rs.getString("holder"))
                .setMaskedNumber(rs.getString("masked_number"))
                .setBrand(CardBrand.valueOf(rs.getString("brand")))
                .setExpDate(rs.getString("exp_date"))
                .setMain(rs.getBoolean("main"))
                .setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime())
                .setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .setUuid(rs.getString("uuid"))
                .setWalletId(rs.getLong("wallet_id"))
                .setActive(rs.getBoolean("active"))
                .setVerifiedByAme(rs.getBoolean("verified_by_ame"))
                .build();
    }
}
