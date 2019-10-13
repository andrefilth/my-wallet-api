package com.amedigital.wallet.repository.mappers;

import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.model.OwnerReference;
import com.amedigital.wallet.model.StatementItem;
import com.amedigital.wallet.model.WalletReference;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.amedigital.wallet.util.MapperUtil.toZonedDateTime;
import static java.util.stream.Collectors.toList;

public class StatementItemMapper implements RowMapper<StatementItem> {

    @Override
    public StatementItem map(ResultSet rs, StatementContext ctx) throws SQLException {
        OrderType type = OrderType.valueOf(rs.getString("order_type"));
        
        Supplier<List<PaymentMethod>> toPaymentMethod = () -> {
            try {
                return Arrays.stream(rs.getString("payment_methods")
                        .split(","))
                        .map(String::trim)
                        .map(PaymentMethod::valueOf)
                        .collect(toList());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        OwnerReference ownerReference = new OwnerReference.Builder()
                .setName(rs.getString("peer_owner_name"))
                .build();

        WalletReference walletReference = new WalletReference.Builder()
                .setType(WalletType.valueOf(rs.getString("peer_wallet_type")))
                .setOwner(ownerReference)
                .build();

        return new StatementItem.Builder()
                .setId(rs.getString("order_uuid"))
                .setType(type)
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTitle(rs.getString("order_title"))
                .setDescription(rs.getString("order_description"))
                .setAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")))
                .setPaymentMethods(toPaymentMethod.get())
                .setOrderReference(rs.getString("reference_order_uuid"))
                .setWalletReference(walletReference)
				.setNetAmountInCents(rs.getLong("transaction_net_amount_in_cents"))
                
                .build();
    }
}
