package com.amedigital.wallet.service.fastcash.request;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CashOutRequest implements Serializable {
	
    private static final long serialVersionUID = -2622149216709188855L;
	
    private final String tid;
    private final String description;
    private final Map<String, Object> customPayload;
    private final Long amount;
    private final String clientName;
    private final String clientEmail;
    private final String clientPhone;
    private final String clientCPF;
    private final Integer bank;
    private final String agency;
    private final String accountNumber;
    
	public CashOutRequest(Builder builder) {
		super();
		this.tid = builder.tid;
		this.description = builder.description;
		this.customPayload = builder.customPayload;
		this.amount = builder.amount;
		this.clientName = builder.clientName;
		this.clientEmail = builder.clientEmail;
		this.clientPhone = builder.clientPhone;
		this.clientCPF = builder.clientCPF;
		this.bank = builder.bank;
		this.agency = builder.agency;
		this.accountNumber = builder.accountNumber;
	}
	
    public static Builder builder() {
        return new Builder();
    }
    
	public String getTid() {
		return tid;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, Object> getCustomPayload() {
		return customPayload;
	}

	public Long getAmount() {
		return amount;
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

	public static final class Builder {
    	
    	private String tid;
        private String description;
        private Map<String, Object> customPayload;
        private Long amount;
        private String clientName;
        private String clientEmail;
        private String clientPhone;
        private String clientCPF;
        private Integer bank;
        private String agency;
        private String accountNumber;


		public Builder setTid(String tid) {
			this.tid = tid;
			return this;
		}

		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder setCustomPayload(Map<String, Object> customPayload) {
			this.customPayload = customPayload;
			return this;
		}

		public Builder setAmount(Long amount) {
			this.amount = amount;
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

		public CashOutRequest build() {
			return new CashOutRequest(this);
		}
    }
	
    @Override
    public String toString() {
    	
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("tid", tid)
                .append("description", description)
                .append("customPayload", customPayload)
                .append("amount", amount)
                .append("clientName", clientName)
                .append("clientEmail", clientEmail)
                .append("clientPhone", clientPhone)
                .append("clientCPF", clientCPF)
                .append("bank", bank)
                .append("agency", agency)
                .append("accountNumber", accountNumber)
                .build();
    }
}
