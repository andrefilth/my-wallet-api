package com.amedigital.wallet.model.transaction;

import static com.amedigital.wallet.constants.enuns.PaymentMethod.BANK_TRANSFER;

import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.amedigital.wallet.constants.enuns.BankTransferStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public class BankTransferTransaction extends Transaction {
	
	private final Long id;
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

	private BankTransferTransaction(Builder builder) {
		super(builder);
		this.id = builder.id;
		this.clientName = builder.clientName;
		this.clientEmail = builder.clientEmail;
		this.clientPhone = builder.clientPhone;
		this.clientCPF = builder.clientCPF;
		this.bank = builder.bank;
		this.agency = builder.agency;
		this.accountNumber = builder.accountNumber;
		this.taxApplied = builder.taxApplied;
		this.bankTransferStatus = builder.bankTransferStatus;
		this.bankTransferType = builder.bankTransferType;
		this.destinationAgency = builder.destinationAgency;
		this.destinationAccount = builder.destinationAccount;
		this.destinationAccountHolder = builder.destinationAccountHolder;
		this.destinationAccountHolderDocument = builder.destinationAccountHolderDocument;
	}

    public static Builder builder() {
        return new Builder();
    }
	
	@Override
    public Builder copy() {
        return new Builder()
                .setId(id)
                .setTransactionId(transactionId)
                .setWalletId(walletId)
                .setUuid(uuid)
                .setOrderUuid(orderUuid)
                .setStatus(status)
                .setType(type)
                .setAmountInCents(amountInCents)
                .setTakeRate(takeRate)
                .setReleaseDate(releaseDate)
                .setLatest(latest)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt)
                .setPeerWalletId(peerWalletId)
                .setPeerTransactionUuid(peerTransactionUuid)
                .setClientName(clientName)
                .setClientEmail(clientEmail)
                .setClientPhone(clientPhone)
                .setClientCPF(clientCPF)
                .setBank(bank)
                .setAgency(agency)
                .setAccountNumber(accountNumber)
                .setTaxApplied(taxApplied)
                .setBankTransferStatus(bankTransferStatus)
                .setBankTransferType(bankTransferType)
                .setDestinationAgency(destinationAgency)
                .setDestinationAccount(destinationAccount)
                .setDestinationAccountHolder(destinationAccountHolder)
                .setDestinationAccountHolderDocument(destinationAccountHolderDocument)
                .setReleaseTime(releaseTime)
                .setReleaseTimeUnit(releaseTimeUnit)
				.setTakeRateUnit(takeRateUnit)
				.setTakeRateAmountInCents(takeRateAmountInCents)
				.setGrossAmountInCents(grossAmountInCents)
				.setNetAmountInCents(netAmountInCents)
				;

    }
    
	public Long getId() {
		return id;
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
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("id", id)
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
				.append("transactionId", transactionId)
				.append("uuid", uuid)
				.append("walletId", walletId)
				.append("walletUuid", walletUuid)
				.append("orderUuid", orderUuid)
				.append("status", status)
				.append("type", type)
				.append("paymentMethod", paymentMethod)
				.append("amountInCents", amountInCents)
				.append("latest", latest)
				.append("createdAt", createdAt)
				.append("updatedAt", updatedAt)
				.append("peerWalletId", peerWalletId)
				.append("peerWalletUuid", peerWalletUuid)
				.append("peerTransactionUuid", peerTransactionUuid)
				.append("takeRate", takeRate)
				.append("releaseDate", releaseDate)
				.append("grossAmountInCents", grossAmountInCents)
				.append("takeRateAmountInCents", takeRateAmountInCents)
				.append("netAmountInCents", netAmountInCents)
				.append("releaseTime", releaseTime)
				.append("takeRateUnit", takeRateUnit)
				.append("releaseTimeUnit", releaseTimeUnit)
				.toString();
	}

	public static final class Builder extends Transaction.TransactionBuilder<BankTransferTransaction.Builder, BankTransferTransaction> {

		private Long id;
		private String clientName;
		private String clientEmail;
		private String clientPhone;
		private String clientCPF;
		private Integer bank;
		private String agency;
		private String accountNumber;
		private Long taxApplied;
		private BankTransferStatus bankTransferStatus;
		private BankTransferType bankTransferType;
		private String destinationAgency;
		private String destinationAccount;
		private String destinationAccountHolder;
		private String destinationAccountHolderDocument;

		public Builder() {
			super(BANK_TRANSFER);
		}

		public Builder setId(Long id) {
			this.id = id;
			return this;
		}

		public Builder setClientName(String clientName) {
			this.clientName = clientName;
			return this;
		}

		public Builder setClientEmail(String clientEmail) {
			this.clientEmail = clientEmail;
			return this;
		}

		public Builder setClientPhone(String clientPhone) {
			this.clientPhone = clientPhone;
			return this;
		}

		public Builder setClientCPF(String clientCPF) {
			this.clientCPF = clientCPF;
			return this;
		}

		public Builder setBank(Integer bank) {
			this.bank = bank;
			return this;
		}

		public Builder setAgency(String agency) {
			this.agency = agency;
			return this;
		}

		public Builder setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
			return this;
		}

		public Builder setBankTransferStatus(BankTransferStatus bankTransferStatus) {
			this.bankTransferStatus = bankTransferStatus;
			return this;
		}
		
		public Builder setBankTransferType(BankTransferType bankTransferType) {
			this.bankTransferType = bankTransferType;
			return this;
		}

		public Builder setTaxApplied(Long taxApplied) {
			this.taxApplied = taxApplied;
			return this;
		}
		
		public Builder setDestinationAgency(String destinationAgency) {
			this.destinationAgency = destinationAgency;
			return this;
		}
		
		public Builder setDestinationAccount(String destinationAccount) {
			this.destinationAccount = destinationAccount;
			return this;
		}
		
		public Builder setDestinationAccountHolder(String destinationAccountHolder) {
			this.destinationAccountHolder = destinationAccountHolder;
			return this;
		}
		
		public Builder setDestinationAccountHolderDocument(String destinationAccountHolderDocument) {
			this.destinationAccountHolderDocument = destinationAccountHolderDocument;
			return this;
		}

		public BankTransferTransaction build() {
			return new BankTransferTransaction(this);
		}
	}
	
	public static enum BankTransferType {
		
	    BANK_CASH_OUT,
	    BANK_CASH_IN;

	    @JsonCreator
	    static BankTransferType of(String value) {
	        try {
	            return valueOf(value);
	        } catch (Exception e) {
	            throw new AmeInvalidInputException("banktransfer_type_parse_error", "Os Types validos sao: " 
	            		+ Arrays.toString(BankTransferType.values()));
	        }
	    }
	}
}
