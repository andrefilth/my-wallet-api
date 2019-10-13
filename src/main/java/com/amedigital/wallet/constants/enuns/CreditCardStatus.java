package com.amedigital.wallet.constants.enuns;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CreditCardStatus {
    CREATED,
    LOCKED,
    NEW_PAYMENT,

    AUTHORIZED,
    UNAUTHORIZED,
    ERROR_TO_AUTHORIZE,
    ACQUIRER_UNAVAILABLE_ERROR,

    CAPTURED,
    CAPTURED_WITH_REFUND,
    ERROR_TO_CAPTURE,

    CANCELLED,
    CANCELLATION_REFUSED,
    CANCELLATION_ERROR,
    REFUNDED,
    CANCELLATION_PENDING,
    UNEXPECTED_ERROR;

    @JsonCreator
    static CreditCardStatus of(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            throw new AmeInvalidInputException("credit_card_status_parse_error", "Os DocumentType válidos são: " +
                    "[" + AUTHORIZED + "," + LOCKED + ", " + CAPTURED + ", " + CANCELLED + ", " + REFUNDED +
                    ", " + UNAUTHORIZED + ", " + ERROR_TO_AUTHORIZE + ", " + ERROR_TO_CAPTURE + ", " + NEW_PAYMENT + "," +
                    " " + ACQUIRER_UNAVAILABLE_ERROR + ", " + CAPTURED_WITH_REFUND + "," + UNEXPECTED_ERROR + " "
                    + CANCELLATION_REFUSED + " " + CANCELLATION_ERROR + "]");
        }
    }


}
