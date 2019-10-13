package com.amedigital.wallet.repository.mappers;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.constants.enuns.WalletType;
import com.amedigital.wallet.model.OrderItem;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.amedigital.wallet.util.MapperUtil.toZonedDateTime;
import static java.util.stream.Collectors.toList;

public class OrderItemMapper implements RowMapper<OrderItem> {
    @Override
    public OrderItem map(ResultSet rs, StatementContext ctx) throws SQLException {

        Supplier<List<String>> toPaymentMethod = () -> {
            try {
                return Arrays.stream(rs.getString("payment_methods")
                        .split(","))
                        .map(String::trim)
                        .collect(toList());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        String transactionType = getString(rs, "transaction_type");
        String walletId = getString(rs, "wallet_id");
        String peerWalletType = getString(rs, "peer_wallet_type");

        return new OrderItem.Builder()
                .setId(rs.getString("order_uuid"))
                .setType(OrderType.valueOf(rs.getString("order_type")))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTitle(rs.getString("order_title"))
                .setDescription(rs.getString("order_description"))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setNsu(rs.getString("order_nsu"))
                .setTransactionType(transactionType != null ? TransactionType.valueOf(transactionType) : null)
                .setWalletId(walletId)
                .setPeerWalletId(getString(rs, "peer_wallet_id"))
                .setReferenceOrderUuid(rs.getString("reference_order_uuid"))
                .setPeerWalletType(peerWalletType != null ? WalletType.valueOf(peerWalletType) : null)
                .setPeerOwnerName(getString(rs,"peer_owner_name"))
                .setPaymentMethods(toPaymentMethod.get())
                .setSecondaryId(rs.getString("order_secondary_id"))
                .build();
    }

    private String getString(ResultSet rs, String columnName) {
        try {
            return rs.getString(columnName);
        }catch (Exception e) {
            return null;
        }

    }
}
