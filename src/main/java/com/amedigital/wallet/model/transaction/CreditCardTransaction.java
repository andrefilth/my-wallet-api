package com.amedigital.wallet.model.transaction;

import com.amedigital.wallet.constants.enuns.CreditCardStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;

import static com.amedigital.wallet.constants.enuns.PaymentMethod.CREDIT_CARD;

public class CreditCardTransaction extends Transaction {

    private final Long id;
    private final String creditCardId;
    private final CreditCardStatus creditCardStatus;
    private final String acquirer;
    private final String tid;
    private final String nsu;
    private final String authorizationNsu;
    private final String captureNsu;
    private final String cancelNsu;
    private final Integer numberOfInstallments;
    private final String installmentType;
    private final String authorizationCode;
    private final String authorizationTid;
    private final String captureTid;
    private final String cancelTid;
    private final String holderName;
    private final String maskedNumber;
    private final String cvv;
    private final String brand;
    private final Integer expirationMonth;
    private final Integer expirationYear;
    private final String currency;
    private final ZonedDateTime authorizationDate;
    private final ZonedDateTime captureDate;
    private final ZonedDateTime cancelDate;
    private final ZonedDateTime refundDate;
    private final String gatewayCancellationReference;
    private final String gatewayResponseMessage;
    private final String gatewayResponseCode;
    private final String gatewayOrderReference;
    private final String gatewayPaymentReference;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final ZonedDateTime creditCardCreatedAt;
    private final ZonedDateTime creditCardUpdatedAt;

    private CreditCardTransaction(Builder builder) {
        super(builder);

        this.id = builder.id;
        this.creditCardId = builder.creditCardId;
        this.creditCardStatus = builder.creditCardStatus;
        this.acquirer = builder.acquirer;
        this.tid = builder.tid;
        this.nsu = builder.nsu;
        this.authorizationNsu = builder.authorizationNsu;
        this.captureNsu = builder.captureNsu;
        this.cancelNsu = builder.cancelNsu;
        this.numberOfInstallments = builder.numberOfInstallments;
        this.installmentType = builder.installmentType;
        this.authorizationCode = builder.authorizationCode;
        this.authorizationTid = builder.authorizationTid;
        this.captureTid = builder.captureTid;
        this.cancelTid = builder.cancelTid;
        this.holderName = builder.holderName;
        this.maskedNumber = builder.maskedNumber;
        this.cvv = builder.cvv;
        this.brand = builder.brand;
        this.expirationMonth = builder.expirationMonth;
        this.expirationYear = builder.expirationYear;
        this.currency = builder.currency;
        this.authorizationDate = builder.authorizationDate;
        this.captureDate = builder.captureDate;
        this.cancelDate = builder.cancelDate;
        this.refundDate = builder.refundDate;
        this.gatewayResponseMessage = builder.gatewayResponseMessage;
        this.gatewayResponseCode = builder.gatewayResponseCode;
        this.gatewayCancellationReference = builder.gatewayCancellationReference;
        this.gatewayOrderReference = builder.gatewayOrderReference;
        this.gatewayPaymentReference = builder.gatewayPaymentReference;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.creditCardCreatedAt = builder.creditCardCreatedAt;
        this.creditCardUpdatedAt = builder.creditCardUpdatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getCreditCardId() {
        return creditCardId;
    }

    public CreditCardStatus getCreditCardStatus() {
        return creditCardStatus;
    }

    public String getAcquirer() {
        return acquirer;
    }

    public String getTid() {
        return tid;
    }

    public String getNsu() {
        return nsu;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public String getInstallmentType() {
        return installmentType;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public String getAuthorizationTid() {
        return authorizationTid;
    }

    public String getCaptureTid() {
        return captureTid;
    }

    public String getCancelTid() {
        return cancelTid;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public String getBrand() {
        return brand;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public String getCurrency() {
        return currency;
    }

    public ZonedDateTime getAuthorizationDate() {
        return authorizationDate;
    }

    public ZonedDateTime getCaptureDate() {
        return captureDate;
    }

    public ZonedDateTime getCancelDate() {
        return cancelDate;
    }

    public ZonedDateTime getRefundDate() {
        return refundDate;
    }

    public String getGatewayResponseMessage() {
        return gatewayResponseMessage;
    }

    public String getGatewayResponseCode() {
        return gatewayResponseCode;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getAuthorizationNsu() {
        return authorizationNsu;
    }

    public String getCaptureNsu() {
        return captureNsu;
    }

    public String getCancelNsu() {
        return cancelNsu;
    }

    public String getGatewayCancellationReference() {
        return gatewayCancellationReference;
    }

    public String getGatewayOrderReference() {
        return gatewayOrderReference;
    }

    public String getGatewayPaymentReference() {
        return gatewayPaymentReference;
    }

    public ZonedDateTime getCreditCardCreatedAt() {
        return creditCardCreatedAt;
    }

    public ZonedDateTime getCreditCardUpdatedAt() {
        return creditCardUpdatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder()
                .setId(id)
                .setUuid(uuid)
                .setWalletId(walletId)
                .setOrderUuid(orderUuid)
                .setStatus(status)
                .setType(type)
                .setAmountInCents(amountInCents)
                .setTakeRate(takeRate)
                .setReleaseDate(releaseDate)
                .setLatest(latest)
                .setTransactionId(transactionId)
                .setReleaseTime(releaseTime)
                .setReleaseTimeUnit(releaseTimeUnit)
                .setCreditCardId(creditCardId)
                .setCreditCardStatus(creditCardStatus)
                .setAcquirer(acquirer)
                .setTid(tid)
                .setNsu(nsu)
                .setNumberOfInstallments(numberOfInstallments)
                .setInstallmentType(installmentType)
                .setAuthorizationCode(authorizationCode)
                .setAuthorizationTid(authorizationTid)
                .setCaptureTid(captureTid)
                .setCancelTid(cancelTid)
                .setHolderName(holderName)
                .setMaskedNumber(maskedNumber)
                .setCvv(cvv)
                .setBrand(brand)
                .setExpirationMonth(expirationMonth)
                .setExpirationYear(expirationYear)
                .setAmountInCents(amountInCents)
                .setCurrency(currency)
                .setAuthorizationDate(authorizationDate)
                .setCaptureDate(captureDate)
                .setCancelDate(cancelDate)
                .setRefundDate(refundDate)
                .setGatewayResponseMessage(gatewayResponseMessage)
                .setGatewayResponseCode(gatewayResponseCode)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt)
                .setGatewayOrderReference(gatewayOrderReference)
                .setGatewayPaymentReference(gatewayPaymentReference)
                .setAuthorizationNsu(authorizationNsu)
                .setCaptureNsu(captureNsu)
                .setCancelNsu(cancelNsu)
                .setGatewayCancellationReference(gatewayCancellationReference)
                .setCreditCardCreatedAt(creditCardCreatedAt)
                .setCreditCardUpdatedAt(creditCardUpdatedAt)
                .setPeerWalletId(peerWalletId)
                .setPeerTransactionUuid(peerTransactionUuid)
                .setTakeRateUnit(takeRateUnit)
                .setGrossAmountInCents(grossAmountInCents)
                .setNetAmountInCents(netAmountInCents)
                .setTakeRateAmountInCents(takeRateAmountInCents);

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("creditCardId", creditCardId)
                .append("creditCardStatus", creditCardStatus)
                .append("acquirer", acquirer)
                .append("tid", tid)
                .append("nsu", nsu)
                .append("authorizationNsu", authorizationNsu)
                .append("captureNsu", captureNsu)
                .append("cancelNsu", cancelNsu)
                .append("numberOfInstallments", numberOfInstallments)
                .append("installmentType", installmentType)
                .append("authorizationCode", authorizationCode)
                .append("authorizationTid", authorizationTid)
                .append("captureTid", captureTid)
                .append("cancelTid", cancelTid)
                .append("holderName", holderName)
                .append("maskedNumber", maskedNumber)
                .append("cvv", cvv)
                .append("brand", brand)
                .append("expirationMonth", expirationMonth)
                .append("expirationYear", expirationYear)
                .append("currency", currency)
                .append("authorizationDate", authorizationDate)
                .append("captureDate", captureDate)
                .append("cancelDate", cancelDate)
                .append("refundDate", refundDate)
                .append("gatewayCancellationReference", gatewayCancellationReference)
                .append("gatewayResponseMessage", gatewayResponseMessage)
                .append("gatewayResponseCode", gatewayResponseCode)
                .append("gatewayOrderReference", gatewayOrderReference)
                .append("gatewayPaymentReference", gatewayPaymentReference)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("creditCardCreatedAt", creditCardCreatedAt)
                .append("creditCardUpdatedAt", creditCardUpdatedAt)
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
                .append("releaseTime", releaseTime)
                .append("takeRateUnit", takeRateUnit)
                .append("releaseTimeUnit", releaseTimeUnit)
                .append("grossAmountInCents", grossAmountInCents)
                .append("netAmountInCents", netAmountInCents)
                .append("takeRateAmountInCents", takeRateAmountInCents)
                .toString();
    }

    public static final class Builder extends Transaction.TransactionBuilder<CreditCardTransaction.Builder, CreditCardTransaction> {

        private Long id;
        private String creditCardId;
        private CreditCardStatus creditCardStatus;
        private String acquirer;
        private String tid;
        private String nsu;
        private String authorizationNsu;
        private String captureNsu;
        private String cancelNsu;
        private Integer numberOfInstallments;
        private String installmentType;
        private String authorizationCode;
        private String authorizationTid;
        private String captureTid;
        private String cancelTid;
        private String holderName;
        private String maskedNumber;
        private String brand;
        private String cvv;
        private Integer expirationMonth;
        private Integer expirationYear;
        private String currency = "BRL";
        private ZonedDateTime authorizationDate;
        private ZonedDateTime captureDate;
        private ZonedDateTime cancelDate;
        private ZonedDateTime refundDate;
        private String gatewayResponseMessage;
        private String gatewayResponseCode;
        private String gatewayCancellationReference;
        private String gatewayOrderReference;
        private String gatewayPaymentReference;
        private ZonedDateTime creditCardUpdatedAt;
        private ZonedDateTime creditCardCreatedAt;

        public Builder() {
            super(CREDIT_CARD);
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setCreditCardId(String creditCardId) {
            this.creditCardId = creditCardId;
            return this;
        }

        public Builder setCreditCardStatus(CreditCardStatus creditCardStatus) {
            this.creditCardStatus = creditCardStatus;
            return this;
        }

        public Builder setAcquirer(String acquirer) {
            this.acquirer = acquirer;
            return this;
        }

        public Builder setTid(String tid) {
            this.tid = tid;
            return this;
        }

        public Builder setNsu(String nsu) {
            this.nsu = nsu;
            return this;
        }

        public Builder setNumberOfInstallments(Integer numberOfInstallments) {
            this.numberOfInstallments = numberOfInstallments;
            return this;
        }

        public Builder setInstallmentType(String installmentType) {
            this.installmentType = installmentType;
            return this;
        }

        public Builder setAuthorizationCode(String authorizationCode) {
            this.authorizationCode = authorizationCode;
            return this;
        }

        public Builder setAuthorizationTid(String authorizationTid) {
            this.authorizationTid = authorizationTid;
            return this;
        }

        public Builder setCaptureTid(String captureTid) {
            this.captureTid = captureTid;
            return this;
        }

        public Builder setCancelTid(String cancelTid) {
            this.cancelTid = cancelTid;
            return this;
        }

        public Builder setHolderName(String holderName) {
            this.holderName = holderName;
            return this;
        }

        public Builder setMaskedNumber(String maskedNumber) {
            this.maskedNumber = maskedNumber;
            return this;
        }

        public Builder setCvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public Builder setBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder setExpirationMonth(Integer expirationMonth) {
            this.expirationMonth = expirationMonth;
            return this;
        }

        public Builder setExpirationYear(Integer expirationYear) {
            this.expirationYear = expirationYear;
            return this;
        }

        public Builder setAmountInCents(Long amountInCents) {
            this.amountInCents = amountInCents;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setAuthorizationDate(ZonedDateTime authorizationDate) {
            this.authorizationDate = authorizationDate;
            return this;
        }

        public Builder setCaptureDate(ZonedDateTime captureDate) {
            this.captureDate = captureDate;
            return this;
        }

        public Builder setCancelDate(ZonedDateTime cancelDate) {
            this.cancelDate = cancelDate;
            return this;
        }

        public Builder setRefundDate(ZonedDateTime refundDate) {
            this.refundDate = refundDate;
            return this;
        }

        public Builder setGatewayResponseMessage(String gatewayResponseMessage) {
            this.gatewayResponseMessage = gatewayResponseMessage;
            return this;
        }

        public Builder setGatewayResponseCode(String gatewayResponseCode) {
            this.gatewayResponseCode = gatewayResponseCode;
            return this;
        }

        public Builder setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setAuthorizationNsu(String authorizationNsu) {
            this.authorizationNsu = authorizationNsu;
            return this;
        }

        public Builder setCaptureNsu(String captureNsu) {
            this.captureNsu = captureNsu;
            return this;
        }

        public Builder setCancelNsu(String cancelNsu) {
            this.cancelNsu = cancelNsu;
            return this;
        }

        public Builder setGatewayCancellationReference(String gatewayCancellationReference) {
            this.gatewayCancellationReference = gatewayCancellationReference;
            return this;
        }

        public Builder setGatewayOrderReference(String gatewayOrderReference) {
            this.gatewayOrderReference = gatewayOrderReference;
            return this;
        }

        public Builder setGatewayPaymentReference(String gatewayPaymentReference) {
            this.gatewayPaymentReference = gatewayPaymentReference;
            return this;
        }

        public Builder setCreditCardUpdatedAt(ZonedDateTime creditCardUpdatedAt) {
            this.creditCardUpdatedAt = creditCardUpdatedAt;
            return this;
        }

        public Builder setCreditCardCreatedAt(ZonedDateTime creditCardCreatedAt) {
            this.creditCardCreatedAt = creditCardCreatedAt;
            return this;
        }

        public CreditCardTransaction build() {
            return new CreditCardTransaction(this);
        }
    }
}
