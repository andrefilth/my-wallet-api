package com.amedigital.wallet.constants.enuns;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionStatus {
    CREATED,
    AUTHORIZED,
    DENIED,
    CAPTURED,
    ERROR,
    CANCELLED,
    PENDING;

    @JsonCreator
    static TransactionStatus of(String value) {
        try {
            return valueOf(value);

        } catch (Exception e) {
            throw new AmeInvalidInputException("transaction_status_parse_error", "Os TransactionStatus válidos são: ["
                    + CREATED + "," + AUTHORIZED + ", " + DENIED + ", " + ERROR + ", " + CAPTURED + ", "
                    + CANCELLED +"]");
        }
    }

}
