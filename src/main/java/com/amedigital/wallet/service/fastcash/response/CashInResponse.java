package com.amedigital.wallet.service.fastcash.response;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CashInResponse implements Serializable {

	private static final long serialVersionUID = -2413776958183650230L;
	
	private String tid;
	private BankResponse destinationBank;
	private String destinationAgency;
	private String destinationAccount;
	private String destinationAccountHolder;
	private String destinationAccountHolderDocument;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public BankResponse getDestinationBank() {
		return destinationBank;
	}

	public void setDestinationBank(BankResponse destinationBank) {
		this.destinationBank = destinationBank;
	}

	public String getDestinationAgency() {
		return destinationAgency;
	}

	public void setDestinationAgency(String destinationAgency) {
		this.destinationAgency = destinationAgency;
	}

	public String getDestinationAccount() {
		return destinationAccount;
	}

	public void setDestinationAccount(String destinationAccount) {
		this.destinationAccount = destinationAccount;
	}

	public String getDestinationAccountHolder() {
		return destinationAccountHolder;
	}

	public void setDestinationAccountHolder(String destinationAccountHolder) {
		this.destinationAccountHolder = destinationAccountHolder;
	}

	public String getDestinationAccountHolderDocument() {
		return destinationAccountHolderDocument;
	}

	public void setDestinationAccountHolderDocument(String destinationAccountHolderDocument) {
		this.destinationAccountHolderDocument = destinationAccountHolderDocument;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("tid", tid)
                .append("destinationBank", destinationBank)
                .append("destinationAgency", destinationAgency)
                .append("destinationAccount", destinationAccount)
                .append("destinationAccountHolder", destinationAccountHolder)
                .append("destinationAccountHolderDocument", destinationAccountHolderDocument)
                .build();
    }
}
