package com.amedigital.wallet.repository.mappers;

import com.amedigital.wallet.constants.enuns.DocumentType;
import com.amedigital.wallet.constants.enuns.WalletType;
import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.model.Wallet;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.amedigital.wallet.util.MapperUtil.toZonedDateTime;

public class WalletMapper implements RowMapper<Wallet> {

    @Override
    public Wallet map(ResultSet rs, StatementContext ctx) throws SQLException {
        Owner owner = Owner.builder()
                .setId(rs.getLong("owner_id"))
                .setUuid(rs.getString("owner_uuid"))
                .setExternalId(rs.getString("owner_external_id"))
                .setName(rs.getString("owner_name"))
                .setEmail(rs.getString("owner_email"))
                .setDocument(rs.getString("owner_document"))
                .setDocumentType(DocumentType.valueOf(rs.getString("owner_document_type")))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("owner_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("owner_updated_at").toLocalDateTime()))
                .build();

        return Wallet.builder()
                .setId(rs.getLong("id"))
                .setUuid(rs.getString("uuid"))
                .setMain(rs.getBoolean("main"))
                .setName(rs.getString("name"))
                .setType(WalletType.valueOf(rs.getString("type")))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("updated_at").toLocalDateTime()))
                .setOwner(owner)
                .build();
    }
}
