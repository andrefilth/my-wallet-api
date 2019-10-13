package com.amedigital.wallet.endoint.response.transaction;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.amedigital.wallet.constants.enuns.BankTransferStatus;
import com.amedigital.wallet.model.transaction.BankTransferTransaction;
import com.amedigital.wallet.model.transaction.BankTransferTransaction.BankTransferType;

public class BankTransferTransactionResponse extends TransactionResponse {

	private final String clientName;
	private final String clientEmail;
	private final String clientPhone;
	private final String clientCPF;
	private final Integer bank;
	private final String agency;
	private final String accountNumber;
	private final Long taxApplied;
	private final BankTransferStatus bankTransferStatus;
	private final BankTransferType bankTransferType;
	private final String destinationAgency;
	private final String destinationAccount;
	private final String destinationAccountHolder;
	private final String destinationAccountHolderDocument;
	
    public BankTransferTransactionResponse(BankTransferTransaction bankTransferTransaction) {
        super(bankTransferTransaction);
        this.clientName = bankTransferTransaction.getClientName();
        this.clientEmail = bankTransferTransaction.getClientEmail();
        this.clientPhone = bankTransferTransaction.getClientPhone();
        this.clientCPF = bankTransferTransaction.getClientCPF();
        this.bank = bankTransferTransaction.getBank();
        this.agency = bankTransferTransaction.getAgency();
        this.accountNumber = bankTransferTransaction.getAccountNumber();
        this.taxApplied = bankTransferTransaction.getTaxApplied();
        this.bankTransferStatus = bankTransferTransaction.getBankTransferStatus();
        this.bankTransferType = bankTransferTransaction.getBankTransferType();
        this.destinationAgency = bankTransferTransaction.getDestinationAgency();
        this.destinationAccount = bankTransferTransaction.getDestinationAccount();
        this.destinationAccountHolder = bankTransferTransaction.getDestinationAccountHolder();
        this.destinationAccountHolderDocument = bankTransferTransaction.getDestinationAccountHolderDocument();
    }

    public String getClientName() {
		return clientName;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public String getClientPhone() {
		return clientPhone;
	}

	public String getClientCPF() {
		return clientCPF;
	}

	public Integer getBank() {
		return bank;
	}

	public String getAgency() {
		return agency;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public Long getTaxApplied() {
		return taxApplied;
	}

	public BankTransferStatus getBankTransferStatus() {
		return bankTransferStatus;
	}

	public BankTransferType getBankTransferType() {
		return bankTransferType;
	}

	public String getDestinationAgency() {
		return destinationAgency;
	}

	public String getDestinationAccount() {
		return destinationAccount;
	}

	public String getDestinationAccountHolder() {
		return destinationAccountHolder;
	}

	public String getDestinationAccountHolderDocument() {
		return destinationAccountHolderDocument;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("id", id)
                .append("type", type)
                .append("paymentMethod", paymentMethod)
                .append("amountInCents", amountInCents)
                .append("status", status)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("clientName", clientName)
                .append("clientEmail", clientEmail)
                .append("clientPhone", clientPhone)
                .append("clientCPF", clientCPF)
                .append("bank", bank)
                .append("agency", agency)
                .append("accountNumber", accountNumber)
                .append("taxApplied", taxApplied)
                .append("bankTransferStatus", bankTransferStatus)
                .append("bankTransferType", bankTransferType)
                .append("destinationAgency", destinationAgency)
                .append("destinationAccount", destinationAccount)
                .append("destinationAccountHolder", destinationAccountHolder)
                .append("destinationAccountHolderDocument", destinationAccountHolderDocument)
				.append("walletId", walletId)
				.append("peerWalletId", peerWalletId)
                .build();
    }

}
