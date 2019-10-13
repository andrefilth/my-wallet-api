package com.amedigital.wallet.service.atom.response;

import com.amedigital.wallet.service.atom.response.enums.CancellationState;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public class CancellationPresenter {

    private CancellationState state;
    private Long amountInCents;
    private String reference;
    private String acquirerResponseCode;
    private String acquirerResponseMessage;
    private String errorMessage;
    private String tid;
    private String nsu;
    private LocalDateTime cancelPendingDate;
    private LocalDateTime cancelDate;
    private LocalDateTime cancelErrorDate;
    private LocalDateTime updatedAt;

    public CancellationState getState() {
        return state;
    }

    public void setState(CancellationState state) {
        this.state = state;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAcquirerResponseCode() {
        return acquirerResponseCode;
    }

    public void setAcquirerResponseCode(String acquirerResponseCode) {
        this.acquirerResponseCode = acquirerResponseCode;
    }

    public String getAcquirerResponseMessage() {
        return acquirerResponseMessage;
    }

    public void setAcquirerResponseMessage(String acquirerResponseMessage) {
        this.acquirerResponseMessage = acquirerResponseMessage;
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

    public String getNsu() {
        return nsu;
    }

    public void setNsu(String nsu) {
        this.nsu = nsu;
    }

    public LocalDateTime getCancelPendingDate() {
        return cancelPendingDate;
    }

    public void setCancelPendingDate(LocalDateTime cancelPendingDate) {
        this.cancelPendingDate = cancelPendingDate;
    }

    public LocalDateTime getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(LocalDateTime cancelDate) {
        this.cancelDate = cancelDate;
    }

    public LocalDateTime getCancelErrorDate() {
        return cancelErrorDate;
    }

    public void setCancelErrorDate(LocalDateTime cancelErrorDate) {
        this.cancelErrorDate = cancelErrorDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("state", state)
                .append("amountInCents", amountInCents)
                .append("reference", reference)
                .append("acquirerResponseCode", acquirerResponseCode)
                .append("acquirerResponseMessage", acquirerResponseMessage)
                .append("errorMessage", errorMessage)
                .append("tid", tid)
                .append("nsu", nsu)
                .append("cancelPendingDate", cancelPendingDate)
                .append("cancelDate", cancelDate)
                .append("cancelDate", cancelDate)
                .append("cancelErrorDate", cancelErrorDate)
                .append("updatedAt", updatedAt)
                .build();
    }
}
