package com.amedigital.wallet.service.atom.response.enums;

public enum PaymentState {

    LOCKED("The method is locked because there is an operation in execution."),
    NEW_PAYMENT("Payment was created with success"),

    AUTHORIZED("Payment was authorize with success"),
    UNAUTHORIZED("Payment was not authorize"),
    ERROR_TO_AUTHORIZE("Occurred an error to authorize the method"),
    ACQUIRER_UNAVAILABLE_ERROR("Acquirer was unavailable, we could not process the transaction."),

    CAPTURED("Payment was captured with success"),
    CAPTURED_WITH_REFUND("Payment was captured with refunds."),
    ERROR_TO_CAPTURE("Occurred an error to finish the method"),

    CANCELED("Payment was canceled with success"),

    REFUNDED("Payment was refunded with success");

    private final String description;

    PaymentState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
