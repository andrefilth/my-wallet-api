package com.amedigital.wallet.service.atom.response;

import com.amedigital.wallet.service.atom.response.enums.AcquirerTransactionStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public class AuthorizationPresenter {

    private AcquirerTransactionStatus status;
    private Long amount;
    private String responseCode;
    private String responseMessage;
    private LocalDateTime authorizationDate;
    private String errorCode;
    private String errorMessage;
    private String tid;
    private String authorizationCode;
    private String nsu;
    private String specificOperationIdentifier;
    private LocalDateTime createdAt;


    public AcquirerTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(AcquirerTransactionStatus status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public LocalDateTime getAuthorizationDate() {
        return authorizationDate;
    }

    public void setAuthorizationDate(LocalDateTime authorizationDate) {
        this.authorizationDate = authorizationDate;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getNsu() {
        return nsu;
    }

    public void setNsu(String nsu) {
        this.nsu = nsu;
    }

    public String getSpecificOperationIdentifier() {
        return specificOperationIdentifier;
    }

    public void setSpecificOperationIdentifier(String specificOperationIdentifier) {
        this.specificOperationIdentifier = specificOperationIdentifier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("status", status)
                .append("amount", amount)
                .append("responseCode", responseCode)
                .append("responseMessage", responseMessage)
                .append("authorizationDate", authorizationDate)
                .append("errorCode", errorCode)
                .append("errorMessage", errorMessage)
                .append("tid", tid)
                .append("authorizationCode", authorizationCode)
                .append("nsu", nsu)
                .append("specificOperationIdentifier", specificOperationIdentifier)
                .append("createdAt", createdAt)
                .build();
    }
}
