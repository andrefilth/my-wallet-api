package com.amedigital.wallet.repository.mappers;

import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.transaction.*;
import com.amedigital.wallet.model.transaction.BankTransferTransaction.BankTransferType;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

import static com.amedigital.wallet.util.MapperUtil.toZonedDateTime;

public class TransactionMapper implements RowMapper<Transaction> {

    @Override
    public Transaction map(ResultSet rs, StatementContext ctx) throws SQLException {

        PaymentMethod paymentMethod = PaymentMethod.valueOf(rs.getString("transaction_payment_method"));

        switch (paymentMethod) {
            case CREDIT_CARD:
                return  createCreditCardTransaction(rs);
            case CASH:
            	return createCashTransaction(rs);
            case BANK_TRANSFER:
            	return createBankTransferTransaction(rs);
			case CASH_BACK:
				return createCashBackTransaction(rs);
            default:
                throw new AmeInternalException("Metodo de pagamento invalido");
        }
    }

	private Transaction createCreditCardTransaction(ResultSet rs) throws SQLException {
		Timestamp transactionReleaseDate = rs.getTimestamp("transaction_release_date");
		Timestamp transactionCreatedAt = rs.getTimestamp("transaction_created_at");
		Timestamp transactionUpdatedAt = rs.getTimestamp("transaction_updated_at");
		
		long peerWalletId = rs.getLong("peer_wallet_id");
		
		Timestamp creditCardCreatedAt = rs.getTimestamp("cc_created_at");
		Timestamp creditCardUpdatedAt = rs.getTimestamp("cc_updated_at");
		Timestamp authorizationDate = rs.getTimestamp("cc_authorization_date");
		Timestamp captureDate = rs.getTimestamp("cc_capture_date");
		Timestamp cancelDate = rs.getTimestamp("cc_cancel_date");
		Timestamp refundDate = rs.getTimestamp("cc_refund_date");

		String releaseTimeUnit = rs.getString("transaction_release_time_unit");
		String taxType = rs.getString("transaction_take_rate_unit");

		return new CreditCardTransaction.Builder()
		        .setTransactionId(rs.getLong("transaction_id"))
		        .setUuid(rs.getString("transaction_uuid"))
		        .setWalletId(rs.getLong("transaction_wallet_id"))
		        .setStatus(TransactionStatus.valueOf(rs.getString("transaction_status")))
		        .setType(TransactionType.valueOf(rs.getString("transaction_type")))
		        .setAmountInCents(rs.getLong("transaction_amount_in_cents"))
		        .setTakeRate(rs.getLong("transaction_take_rate"))
		        .setPeerTransactionUuid(rs.getString("peer_transaction_uuid"))
		        .setReleaseDate(toZonedDateTime(transactionReleaseDate != null ? transactionReleaseDate.toLocalDateTime() : null))
		        .setLatest(rs.getBoolean("transaction_latest"))
		        .setCreatedAt(toZonedDateTime(transactionCreatedAt != null ? transactionCreatedAt.toLocalDateTime() : null))
		        .setUpdatedAt(toZonedDateTime(transactionUpdatedAt != null ? transactionUpdatedAt.toLocalDateTime() : null))
		        .setId(rs.getLong("creditcard_id"))
		        .setCreditCardId(rs.getString("credit_card_id"))
		        .setCreditCardStatus(CreditCardStatus.valueOf(rs.getString("creditcard_status")))
		        .setGatewayOrderReference(rs.getString("cc_gateway_order_reference"))
		        .setGatewayPaymentReference(rs.getString("cc_gateway_payment_reference"))
		        .setAcquirer(rs.getString("cc_acquirer"))
		        .setTid(rs.getString("cc_tid"))
		        .setNsu(rs.getString("cc_nsu"))
		        .setAuthorizationNsu(rs.getString("cc_authorization_nsu"))
		        .setCaptureNsu(rs.getString("cc_capture_nsu"))
		        .setNumberOfInstallments(rs.getInt("cc_number_of_installments"))
		        .setInstallmentType(rs.getString("cc_installment_type"))
		        .setAuthorizationCode(rs.getString("cc_authorization_code"))
		        .setAuthorizationTid(rs.getString("cc_authorization_tid"))
		        .setCaptureTid(rs.getString("cc_capture_tid"))
		        .setCancelTid(rs.getString("cc_cancel_tid"))
		        .setHolderName(rs.getString("cc_holder_name"))
		        .setMaskedNumber(rs.getString("cc_masked_number"))
		        .setBrand(rs.getString("cc_brand"))
		        .setExpirationMonth(rs.getInt("cc_expiration_month"))
		        .setExpirationYear(rs.getInt("cc_expiration_year"))
		        .setCurrency(rs.getString("cc_currency"))
		        .setAuthorizationDate(toZonedDateTime(authorizationDate != null ? authorizationDate.toLocalDateTime() : null))
		        .setCaptureDate(toZonedDateTime(captureDate != null ? captureDate.toLocalDateTime() : null))
		        .setCancelDate(toZonedDateTime(cancelDate != null ? cancelDate.toLocalDateTime() : null))
		        .setReleaseDate(toZonedDateTime(refundDate != null ? refundDate.toLocalDateTime() : null))
		        .setGatewayResponseMessage(rs.getString("cc_gateway_response_message"))
		        .setGatewayCancellationReference(rs.getString("cc_gateway_cancellation_reference"))
		        .setGatewayResponseCode(rs.getString("cc_gateway_response_code"))
		        .setCreditCardCreatedAt(toZonedDateTime(creditCardCreatedAt != null ? creditCardCreatedAt.toLocalDateTime() : null))
		        .setCreditCardUpdatedAt(toZonedDateTime(creditCardUpdatedAt != null ? creditCardUpdatedAt.toLocalDateTime() : null))
		        .setPeerWalletId(peerWalletId)
				.setWalletUuid(rs.getString("transaction_wallet_uuid"))

				.setGrossAmountInCents(rs.getLong("transaction_gross_amount_in_cents"))
				.setNetAmountInCents(rs.getLong("transaction_net_amount_in_cents"))
				.setTakeRateAmountInCents(rs.getLong("transaction_take_rate_amount_in_cents"))

				.setReleaseTime(rs.getLong("transaction_release_time"))
				.setReleaseTimeUnit(releaseTimeUnit != null ? ChronoUnit.valueOf(releaseTimeUnit.toUpperCase()) : null)
				.setTakeRateUnit(taxType != null ? TakeRateUnit.valueOf(taxType) : null)


		        .build();
	}

	private Transaction createCashTransaction(ResultSet rs) throws SQLException {
		Timestamp transactionReleaseDate = rs.getTimestamp("transaction_release_date");
		Timestamp transactionCreatedAt = rs.getTimestamp("transaction_created_at");
		Timestamp transactionUpdatedAt = rs.getTimestamp("transaction_updated_at");
		
		long peerWalletId = rs.getLong("peer_wallet_id");
		
		Timestamp cashCreatedAt = rs.getTimestamp("cash_created_at");
		Timestamp cashUpdatedAt = rs.getTimestamp("cash_updated_at");

		String releaseTimeUnit = rs.getString("transaction_release_time_unit");
		String taxType = rs.getString("transaction_take_rate_unit");

		return CashTransaction.builder()
		        .setTransactionId(rs.getLong("transaction_id"))
		        .setUuid(rs.getString("transaction_uuid"))
		        .setOrderUuid(rs.getString("transaction_order_uuid"))
		        .setWalletId(rs.getLong("transaction_wallet_id"))
		        .setStatus(TransactionStatus.valueOf(rs.getString("transaction_status")))
		        .setType(TransactionType.valueOf(rs.getString("transaction_type")))
		        .setAmountInCents(rs.getLong("transaction_amount_in_cents"))
		        .setTakeRate(rs.getLong("transaction_take_rate"))
		        .setPeerTransactionUuid(rs.getString("peer_transaction_uuid"))
		        .setReleaseDate(toZonedDateTime(transactionReleaseDate != null ? transactionReleaseDate.toLocalDateTime() : null))
		        .setLatest(rs.getBoolean("transaction_latest"))
		        .setCreatedAt(toZonedDateTime(transactionCreatedAt != null ? transactionCreatedAt.toLocalDateTime() : null))
		        .setUpdatedAt(toZonedDateTime(transactionUpdatedAt != null ? transactionUpdatedAt.toLocalDateTime() : null))
		        .setId(rs.getLong("cash_id"))
		        .setCashStatus(CashStatus.valueOf(rs.getString("cash_status")))
		        .setCashCreatedAt(toZonedDateTime(cashCreatedAt != null ? cashCreatedAt.toLocalDateTime() : null))
		        .setCashCreatedAt(toZonedDateTime(cashUpdatedAt != null ? cashUpdatedAt.toLocalDateTime() : null))
		        .setPeerWalletId(peerWalletId)
				.setWalletUuid(rs.getString("transaction_wallet_uuid"))
				.setPeerWalletUuid(rs.getString("transaction_peer_wallet_uuid"))

				.setGrossAmountInCents(rs.getLong("transaction_gross_amount_in_cents"))
				.setNetAmountInCents(rs.getLong("transaction_net_amount_in_cents"))
				.setTakeRateAmountInCents(rs.getLong("transaction_take_rate_amount_in_cents"))

				.setReleaseTime(rs.getLong("transaction_release_time"))
				.setReleaseTimeUnit(releaseTimeUnit != null ? ChronoUnit.valueOf(releaseTimeUnit.toUpperCase()) : null)
				.setTakeRateUnit(taxType != null ? TakeRateUnit.valueOf(taxType) : null)

		        .build();
	}

	private Transaction createCashBackTransaction(ResultSet rs) throws SQLException {
		Timestamp transactionReleaseDate = rs.getTimestamp("transaction_release_date");
		Timestamp transactionCreatedAt = rs.getTimestamp("transaction_created_at");
		Timestamp transactionUpdatedAt = rs.getTimestamp("transaction_updated_at");

		long peerWalletId = rs.getLong("peer_wallet_id");

		Timestamp cashCreatedAt = rs.getTimestamp("cash_back_created_at");
		Timestamp cashUpdatedAt = rs.getTimestamp("cash_back_updated_at");

		String releaseTimeUnit = rs.getString("transaction_release_time_unit");
		String taxType = rs.getString("transaction_take_rate_unit");

		return CashBackTransaction.builder()
				.setTransactionId(rs.getLong("transaction_id"))
				.setUuid(rs.getString("transaction_uuid"))
				.setOrderUuid(rs.getString("transaction_order_uuid"))
				.setWalletId(rs.getLong("transaction_wallet_id"))
				.setStatus(TransactionStatus.valueOf(rs.getString("transaction_status")))
				.setType(TransactionType.valueOf(rs.getString("transaction_type")))
				.setAmountInCents(rs.getLong("transaction_amount_in_cents"))
				.setTakeRate(rs.getLong("transaction_take_rate"))
				.setPeerTransactionUuid(rs.getString("peer_transaction_uuid"))
				.setReleaseDate(toZonedDateTime(transactionReleaseDate != null ? transactionReleaseDate.toLocalDateTime() : null))
				.setLatest(rs.getBoolean("transaction_latest"))
				.setCreatedAt(toZonedDateTime(transactionCreatedAt != null ? transactionCreatedAt.toLocalDateTime() : null))
				.setUpdatedAt(toZonedDateTime(transactionUpdatedAt != null ? transactionUpdatedAt.toLocalDateTime() : null))
				.setId(rs.getLong("cash_back_id"))
				.setCashStatus(CashBackStatus.valueOf(rs.getString("cash_back_status")))
				.setCashCreatedAt(toZonedDateTime(cashCreatedAt != null ? cashCreatedAt.toLocalDateTime() : null))
				.setCashCreatedAt(toZonedDateTime(cashUpdatedAt != null ? cashUpdatedAt.toLocalDateTime() : null))
				.setPeerWalletId(peerWalletId)
				.setWalletUuid(rs.getString("transaction_wallet_uuid"))

				.setGrossAmountInCents(rs.getLong("transaction_gross_amount_in_cents"))
				.setNetAmountInCents(rs.getLong("transaction_net_amount_in_cents"))
				.setTakeRateAmountInCents(rs.getLong("transaction_take_rate_amount_in_cents"))

				.setReleaseTime(rs.getLong("transaction_release_time"))
				.setReleaseTimeUnit(releaseTimeUnit != null ? ChronoUnit.valueOf(releaseTimeUnit.toUpperCase()) : null)
				.setTakeRateUnit(taxType != null ? TakeRateUnit.valueOf(taxType) : null)

				.build();
	}
	
	private Transaction createBankTransferTransaction(ResultSet rs) throws SQLException {
		Timestamp transactionReleaseDate = rs.getTimestamp("transaction_release_date");
		Timestamp transactionCreatedAt = rs.getTimestamp("transaction_created_at");
		Timestamp transactionUpdatedAt = rs.getTimestamp("transaction_updated_at");
		
		long peerWalletId = rs.getLong("peer_wallet_id");

		String releaseTimeUnit = rs.getString("transaction_release_time_unit");
		String taxType = rs.getString("transaction_take_rate_unit");
		
		return BankTransferTransaction.builder()
				.setTransactionId(rs.getLong("transaction_id"))
				.setUuid(rs.getString("transaction_uuid"))
				.setOrderUuid(rs.getString("transaction_order_uuid"))
				.setWalletId(rs.getLong("transaction_wallet_id"))
				.setStatus(TransactionStatus.valueOf(rs.getString("transaction_status")))
				.setType(TransactionType.valueOf(rs.getString("transaction_type")))
				.setAmountInCents(rs.getLong("transaction_amount_in_cents"))
				.setTakeRate(rs.getLong("transaction_take_rate"))
				.setPeerTransactionUuid(rs.getString("peer_transaction_uuid"))
				.setReleaseDate(toZonedDateTime(transactionReleaseDate != null ? transactionReleaseDate.toLocalDateTime() : null))
				.setLatest(rs.getBoolean("transaction_latest"))
				.setCreatedAt(toZonedDateTime(transactionCreatedAt != null ? transactionCreatedAt.toLocalDateTime() : null))
				.setUpdatedAt(toZonedDateTime(transactionUpdatedAt != null ? transactionUpdatedAt.toLocalDateTime() : null))
				.setPeerWalletId(peerWalletId)
				.setWalletUuid(rs.getString("transaction_wallet_uuid"))
				.setId(rs.getLong("banktransfer_id"))
				.setClientName(rs.getString("banktransfer_client_name"))
				.setClientEmail(rs.getString("banktransfer_client_email"))
				.setClientPhone(rs.getString("banktransfer_client_phone"))
				.setClientCPF(rs.getString("banktransfer_client_cpf"))
				.setBank(rs.getInt("banktransfer_bank"))
				.setAgency(rs.getString("banktransfer_agency"))
				.setAccountNumber(rs.getString("banktransfer_account_number"))
				.setBankTransferStatus(BankTransferStatus.valueOf(rs.getString("banktransfer_status")))
				.setBankTransferType(BankTransferType.valueOf(rs.getString("banktransfer_type")))
				.setDestinationAgency(rs.getString("banktransfer_destination_agency"))
				.setDestinationAccount(rs.getString("banktransfer_destination_account"))
				.setDestinationAccountHolder(rs.getString("banktransfer_destination_account_holder"))
				.setDestinationAccountHolderDocument(rs.getString("banktransfer_destination_account_holder_document"))
				.setTaxApplied(rs.getLong("banktransfer_tax_applied"))

				.setGrossAmountInCents(rs.getLong("transaction_gross_amount_in_cents"))
				.setNetAmountInCents(rs.getLong("transaction_net_amount_in_cents"))
				.setTakeRateAmountInCents(rs.getLong("transaction_take_rate_amount_in_cents"))

				.setReleaseTime(rs.getLong("transaction_release_time"))
				.setReleaseTimeUnit(releaseTimeUnit != null ? ChronoUnit.valueOf(releaseTimeUnit.toUpperCase()) : null)
				.setTakeRateUnit(taxType != null ? TakeRateUnit.valueOf(taxType) : null)


				.build();
	}
}
