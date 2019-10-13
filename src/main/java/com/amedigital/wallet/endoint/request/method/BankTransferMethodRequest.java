package com.amedigital.wallet.endoint.request.method;

import static com.amedigital.wallet.constants.enuns.PaymentMethod.BANK_TRANSFER;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.amedigital.wallet.model.transaction.BankTransferTransaction.BankTransferType;

public class BankTransferMethodRequest extends MethodRequest {

	private String clientName;
	private String clientEmail;
	private String clientPhone;
	private String clientCPF;
	private Integer bank;
	private String agency;
	private String accountNumber;
	private Long taxApplied;
	private BankTransferType bankTransferType;
	
	public BankTransferMethodRequest() {
		 super(BANK_TRANSFER);
	}

    public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getClientPhone() {
		return clientPhone;
	}

	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}

	public String getClientCPF() {
		return clientCPF;
	}

	public void setClientCPF(String clientCPF) {
		this.clientCPF = clientCPF;
	}

	public Integer getBank() {
		return bank;
	}

	public void setBank(Integer bank) {
		this.bank = bank;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Long getTaxApplied() {
		return taxApplied;
	}

	public void setTaxApplied(Long taxApplied) {
		this.taxApplied = taxApplied;
	}
	
	public BankTransferMethodRequest setBankTransferType(BankTransferType bankTransferType) {
		this.bankTransferType = bankTransferType;
		return this;
	}

	public BankTransferType getBankTransferType() {
		return bankTransferType;
	}
	
	@Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("type", type)
                .append("amountInCents", amountInCents)
                .append("clientName", clientName)
                .append("clientEmail", clientEmail)
                .append("clientPhone", clientPhone)
                .append("clientCPF", clientCPF)
                .append("bank", bank)
                .append("agency", agency)
                .append("accountNumber", accountNumber)
                .append("taxApplied", taxApplied)
                .append("bankTransferType", bankTransferType)
                .build();
    }
}
