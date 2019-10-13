package com.amedigital.wallet.repository.mappers;

import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.model.Owner;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.primary.*;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.model.order.secondary.RefundOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.amedigital.wallet.util.MapperUtil.toZonedDateTime;
import static java.util.stream.Collectors.toList;

public class OrderMapper implements RowMapper<Order> {

    @Override
    public Order map(ResultSet rs, StatementContext ctx) throws SQLException {

        Action action = new Action.Builder()
                .setId(rs.getLong("action_id"))
                .setParentId(rs.getLong("action_parent_action_id"))
                .setType(ActionType.valueOf(rs.getString("action_type")))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("action_created_at") == null ? null : rs.getTimestamp("action_created_at").toLocalDateTime()))
                .build();


        OrderType type = OrderType.valueOf(rs.getString("order_type"));

        Supplier<List<String>> toPaymentMethod = () -> {
            try {
                return Arrays.stream(rs.getString("order_payment_methods")
                        .split(","))
                        .map(String::trim)
                        .distinct()
                        .collect(toList());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

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

        Wallet wallet;

        try {
            wallet = Wallet.builder()
                    .setId(rs.getLong("wallet_id"))
                    .setUuid(rs.getString("wallet_uuid"))
                    .setMain(rs.getBoolean("wallet_main"))
                    .setName(rs.getString("wallet_name"))
                    .setType(WalletType.valueOf(rs.getString("wallet_type")))
                    .setCreatedAt(toZonedDateTime(rs.getTimestamp("wallet_created_at").toLocalDateTime()))
                    .setUpdatedAt(toZonedDateTime(rs.getTimestamp("wallet_updated_at").toLocalDateTime()))
                    .setOwner(owner)
                    .build();

        } catch (SQLException ex) {
            wallet = Wallet.builder().build();
            
        }

        switch (type) {
            case PURCHASE:
                return createPurchaseOrder(rs, action, toPaymentMethod, owner, wallet);
            case CASH_IN:
                return createCashInOrder(rs, action, toPaymentMethod, owner, wallet);
            case TRANSFER_BETWEEN_WALLETS:
                return createTransferBetweenWalletsOrder(rs, action, toPaymentMethod, owner, wallet);
            case RELEASE:
                return createReleaseOrder(rs, action, toPaymentMethod, owner, wallet);
            case CASH_BACK:
                return createCashBackOrder(rs, action, toPaymentMethod, owner, wallet);
            case REFUND:
                return createRefundOrder(rs, action, toPaymentMethod, owner, wallet);
            case GIFT_CASH_IN:
                return createGiftCashInOrder(rs, action, toPaymentMethod, owner, wallet);
            case CASH_OUT:
            	return createCashOutOrder(rs, action, toPaymentMethod, owner, wallet);
            case STORE_CASH_IN:
                return createStoreCashInOrder(rs, action, toPaymentMethod, owner, wallet);
            case STORE_CASH_OUT:
            	return createStoreCashOutOrder(rs, action, toPaymentMethod, owner, wallet);
            case BANK_CASH_IN:
            	return createBankCashInOrder(rs, action, toPaymentMethod, owner, wallet);
            default:
                return null;
        }
    }

    private Order createPurchaseOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new PurchaseOrder.Builder()
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setReferenceOrderUuid(rs.getString("order_reference_order_uuid"))
                .setPaymentMethods(toPaymentMethod.get())
                .setNsu(rs.getString("order_nsu"))
                .setCreatedByOwner(owner)
                .setCreatedByWallet(wallet)
                .build();
    }

    private Order createCashInOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new CashInOrder.Builder()
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setReferenceOrderUuid(rs.getString("order_reference_order_uuid"))
                .setPaymentMethods(toPaymentMethod.get())
                .setNsu(rs.getString("order_nsu"))
                .setCreatedByOwner(owner)
                .setCreatedByWallet(wallet)
                .build();
    }

    private Order createGiftCashInOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new GiftCashInOrder.Builder()
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setReferenceOrderUuid(rs.getString("order_reference_order_uuid"))
                .setPaymentMethods(toPaymentMethod.get())
                .setNsu(rs.getString("order_nsu"))
                .setCreatedByOwner(owner)
                .build();
    }

    private Order createTransferBetweenWalletsOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new TransferBetweenWalletsOrder.Builder()
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setReferenceOrderUuid(rs.getString("order_reference_order_uuid"))
                .setPaymentMethods(toPaymentMethod.get())
                .setNsu(rs.getString("order_nsu"))
                .setCreatedByOwner(owner)
                .setCreatedByWallet(wallet)
                .build();
    }

    private Order createReleaseOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new ReleaseOrder.Builder(rs.getString("order_reference_order_uuid"))
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setSecondaryId(rs.getString("order_secondary_id"))
                .setNsu(rs.getString("order_nsu"))
                .setPaymentMethods(toPaymentMethod.get())
                .setCreatedByOwner(owner)
                .setCreatedByWallet(wallet)
                .build();
    }

    private Order createCashBackOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new CashbackOrder.Builder(rs.getString("order_reference_order_uuid"))
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setSecondaryId(rs.getString("order_secondary_id"))
                .setPaymentMethods(toPaymentMethod.get())
                .setNsu(rs.getString("order_nsu"))
                .setCreatedByOwner(owner)
                .setCreatedByWallet(wallet)
                .build();
    }

    private Order createRefundOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new RefundOrder.Builder(rs.getString("order_reference_order_uuid"))
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setSecondaryId(rs.getString("order_secondary_id"))
                .setPaymentMethods(toPaymentMethod.get())
                .setNsu(rs.getString("order_nsu"))
                .setCreatedByOwner(owner)
                .setCreatedByWallet(wallet)
                .build();
    }

    private Order createCashOutOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new CashOutOrder.Builder()
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setReferenceOrderUuid(rs.getString("order_reference_order_uuid"))
                .setPaymentMethods(toPaymentMethod.get())
                .setNsu(rs.getString("order_nsu"))
                .setCreatedByOwner(owner)
                .setCreatedByWallet(wallet)
                .build();
    }

    private Order createStoreCashInOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new StoreCashInOrder.Builder()
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setReferenceOrderUuid(rs.getString("order_reference_order_uuid"))
                .setPaymentMethods(toPaymentMethod.get())
                .setNsu(rs.getString("order_nsu"))
                .setCreatedByOwner(owner)
                .setCreatedByWallet(wallet)
                .build();
    }
    
    private Order createStoreCashOutOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
    	return new StoreCashOutOrder.Builder()
    			.setId(rs.getLong("order_id"))
    			.setUuid(rs.getString("order_uuid"))
    			.setStatus(OrderStatus.valueOf(rs.getString("order_status")))
    			.setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
    			.setTitle(rs.getString("order_title"))
    			.setAction(action)
    			.setDescription(rs.getString("order_description"))
    			.setOrderDetailUuid(rs.getString("order_detail_uuid"))
    			.setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
    			.setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
    			.setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
    			.setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
    			.setReferenceOrderUuid(rs.getString("order_reference_order_uuid"))
    			.setPaymentMethods(toPaymentMethod.get())
    			.setNsu(rs.getString("order_nsu"))
    			.setCreatedByOwner(owner)
    			.setCreatedByWallet(wallet)
    			.build();
    }
    
    private Order createBankCashInOrder(ResultSet rs, Action action, Supplier<List<String>> toPaymentMethod, Owner owner, Wallet wallet) throws SQLException {
        return new BankCashInOrder.Builder()
                .setId(rs.getLong("order_id"))
                .setUuid(rs.getString("order_uuid"))
                .setStatus(OrderStatus.valueOf(rs.getString("order_status")))
                .setTotalAmountInCents(rs.getLong("order_total_amount_in_cents"))
                .setTitle(rs.getString("order_title"))
                .setAction(action)
                .setDescription(rs.getString("order_description"))
                .setOrderDetailUuid(rs.getString("order_detail_uuid"))
                .setAuthorizationMethod(AuthorizationMethod.valueOf(rs.getString("order_authorization_method")))
                .setCreatedByWalletId(rs.getLong("order_created_by_wallet_id"))
                .setCreatedAt(toZonedDateTime(rs.getTimestamp("order_created_at").toLocalDateTime()))
                .setUpdatedAt(toZonedDateTime(rs.getTimestamp("order_updated_at").toLocalDateTime()))
                .setReferenceOrderUuid(rs.getString("order_reference_order_uuid"))
                .setPaymentMethods(toPaymentMethod.get())
                .setNsu(rs.getString("order_nsu"))
                .setCreatedByOwner(owner)
                .setCreatedByWallet(wallet)
                .build();
    }
}