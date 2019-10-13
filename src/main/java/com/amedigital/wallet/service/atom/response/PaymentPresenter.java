package com.amedigital.wallet.service.atom.response;

import com.amedigital.wallet.service.atom.response.enums.Acquirer;
import com.amedigital.wallet.service.atom.response.enums.Currency;
import com.amedigital.wallet.service.atom.response.enums.InstallmentType;
import com.amedigital.wallet.service.atom.response.enums.PaymentState;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentPresenter {

    private PaymentState state;
    private String orderReference;
    private String paymentReference;
    private String authorizationCode;
    private Long amountInCents;
    private Long authorizedAmountInCents;
    private Long capturedAmountInCents;
    private Integer installments;
    private InstallmentType installmentType;
    private Currency currency;
    private Acquirer acquirer;
    private String affiliationCode;
    private String terminalName;
    private String businessUnit;
    private String nsu;
    private String tid;
    private String responseCode;
    private String responseMessage;
    private CreditCardPresenter creditCard;
    private List<CancellationPresenter> cancellations;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime authorizationDate;
    private LocalDateTime captureDate;
    private LocalDateTime cancelDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String softDescriptor;
    private List<AuthorizationPresenter> authorizations;
    private List<CapturePresenter> captures;

    public PaymentState getState() {
        return state;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public Long getAuthorizedAmountInCents() {
        return authorizedAmountInCents;
    }

    public Long getCapturedAmountInCents() {
        return capturedAmountInCents;
    }

    public Integer getInstallments() {
        return installments;
    }

    public InstallmentType getInstallmentType() {
        return installmentType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Acquirer getAcquirer() {
        return acquirer;
    }

    public String getAffiliationCode() {
        return affiliationCode;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public String getNsu() {
        return nsu;
    }

    public String getTid() {
        return tid;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public CreditCardPresenter getCreditCard() {
        return creditCard;
    }

    public List<CancellationPresenter> getCancellations() {
        return cancellations;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getAuthorizationDate() {
        return authorizationDate;
    }

    public LocalDateTime getCaptureDate() {
        return captureDate;
    }

    public LocalDateTime getCancelDate() {
        return cancelDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getSoftDescriptor() {
        return softDescriptor;
    }

    public List<AuthorizationPresenter> getAuthorizations() {
        return authorizations;
    }

    public List<CapturePresenter> getCaptures() {
        return captures;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("state", state)
                .append("orderReference", orderReference)
                .append("paymentReference", paymentReference)
                .append("authorizationCode", authorizationCode)
                .append("amountInCents", amountInCents)
                .append("authorizedAmountInCents", authorizedAmountInCents)
                .append("capturedAmountInCents", capturedAmountInCents)
                .append("installments", installments)
                .append("installmentType", installmentType)
                .append("currency", currency)
                .append("acquirer", acquirer)
                .append("affiliationCode", affiliationCode)
                .append("terminalName", terminalName)
                .append("businessUnit", businessUnit)
                .append("nsu", nsu)
                .append("tid", tid)
                .append("responseCode", responseCode)
                .append("responseMessage", responseMessage)
                .append("creditCard", creditCard)
                .append("cancellations", cancellations)
                .append("errorCode", errorCode)
                .append("errorMessage", errorMessage)
                .append("authorizationDate", authorizationDate)
                .append("captureDate", captureDate)
                .append("cancelDate", cancelDate)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("softDescriptor", softDescriptor)
                .append("authorizations", authorizations)
                .append("captures", captures)
                .build();
    }
}
