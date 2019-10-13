package com.amedigital.wallet.service.fastcash.converters;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amedigital.wallet.constants.enuns.BankTransferStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.model.transaction.BankTransferTransaction;
import com.amedigital.wallet.service.fastcash.request.CashInRequest;
import com.amedigital.wallet.service.fastcash.response.CashInResponse;

public class CashInRequestConverter {
	
	private static final Logger LOG = LoggerFactory.getLogger(CashInRequestConverter.class);
	
	public static CashInRequest fromTransaction(BankTransferTransaction bankTransferTransaction) {
	
		return CashInRequest.builder()
			.setTid(bankTransferTransaction.getOrderUuid())
			.setDescription("Transferencia Bank CashIn")
			.setCustomPayload(Collections.emptyMap())
			.setAmount(bankTransferTransaction.getAmountInCents())
			.setClientName(bankTransferTransaction.getClientName())
			.setClientEmail(bankTransferTransaction.getClientEmail())
			.setClientPhone(bankTransferTransaction.getClientPhone())
			.setClientCPF(bankTransferTransaction.getClientCPF())
			.setBank(bankTransferTransaction.getBank())
			.setAgency(bankTransferTransaction.getAgency())
			.setAccountNumber(bankTransferTransaction.getAccountNumber())
			.build();
	}
	
	public static BankTransferTransaction toTransaction(BankTransferTransaction transaction, CashInResponse cashInResponse) {
		
		LOG.info("Convertendo CashInResponse [{}]", cashInResponse);
		
		return transaction.copy()
				.setStatus(TransactionStatus.AUTHORIZED)
				.setBankTransferStatus(BankTransferStatus.AUTHORIZED)
				.setDestinationAgency(cashInResponse.getDestinationAgency())
				.setDestinationAccount(cashInResponse.getDestinationAccount())
				.setDestinationAccountHolder(cashInResponse.getDestinationAccountHolder())
				.setDestinationAccountHolderDocument(cashInResponse.getDestinationAccountHolderDocument())
				.build();
	}
}
