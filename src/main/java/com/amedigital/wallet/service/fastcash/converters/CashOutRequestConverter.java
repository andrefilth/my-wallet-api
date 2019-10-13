package com.amedigital.wallet.service.fastcash.converters;

import java.util.Collections;

import com.amedigital.wallet.constants.enuns.BankTransferStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.model.transaction.BankTransferTransaction;
import com.amedigital.wallet.service.fastcash.request.CashOutRequest;

public class CashOutRequestConverter {
	
	public static CashOutRequest fromTransaction(BankTransferTransaction bankTransferTransaction) {
	
		return CashOutRequest.builder()
			.setTid(bankTransferTransaction.getOrderUuid())
			.setDescription("Transferencia CashOut")
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
	
	public static BankTransferTransaction toTransaction(BankTransferTransaction transaction) {
		
		BankTransferTransaction.Builder builder = transaction.copy();
		
		builder.setStatus(TransactionStatus.AUTHORIZED).setBankTransferStatus(BankTransferStatus.AUTHORIZED);
		
		return builder.build();
	}
}
