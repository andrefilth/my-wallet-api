package com.amedigital.wallet.endoint.converter.order.primary;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amedigital.wallet.constants.enuns.AuthorizationMethod;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.method.RouterMethodRequestConverter;
import com.amedigital.wallet.endoint.request.method.BankTransferMethodRequest;
import com.amedigital.wallet.endoint.request.order.BankCashInOrderRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.primary.BankCashInOrder;
import com.amedigital.wallet.model.transaction.BankTransferTransaction.BankTransferType;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.util.TransactionUtil;

@Component
public class BankCashInOrderRequestConverter implements PrimaryRequestConverter<BankCashInOrderRequest, BankCashInOrder> {

	private final RouterMethodRequestConverter methodConverter;

	@Autowired
	public BankCashInOrderRequestConverter(RouterMethodRequestConverter methodConverter) {
		this.methodConverter = methodConverter;
	}

	@Override
	public BankCashInOrder from(BankCashInOrderRequest orderRequest, RequestContext requestContext) {

		Wallet wallet = requestContext.getTokenWallet().orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet da sessão."));

		ZonedDateTime now = ZonedDateTime.now();
		String orderUuid = UUID.randomUUID().toString();

		List<Transaction> transactions = orderRequest
				.getPaymentMethods()
				.stream()
				.map(methodRequest -> ((BankTransferMethodRequest) methodRequest).setBankTransferType(BankTransferType.BANK_CASH_IN))
				.map(methodRequest -> methodConverter.from(methodRequest, requestContext))
				.collect(Collectors.toList());

		return new BankCashInOrder.Builder()
				.setCreatedByWalletId(wallet.getId().get())
				.setUuid(orderUuid)
				.setNsu(TransactionUtil.createNsu())
				.setTitle(orderRequest.getTitle())
				.setDescription(orderRequest.getDescription())
				.setTotalAmountInCents(orderRequest.getTotalAmountInCents())
				.setTransactions(transactions)
				.setCustomPayload(orderRequest.getCustomPayload())
				.setAuthorizationMethod(AuthorizationMethod.NONE)
				.setCreatedAt(now)
				.setUpdatedAt(now)
				.build();
	}

}
