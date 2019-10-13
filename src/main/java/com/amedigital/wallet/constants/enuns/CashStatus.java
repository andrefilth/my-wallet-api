package com.amedigital.wallet.constants.enuns;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CashStatus {
    CREATED,
    AUTHORIZED,
    DENIED,
    CAPTURED,
    ERROR,
    CANCELLED,
    PENDING;


    @JsonCreator
    static CashStatus of(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            throw new AmeInvalidInputException("cash_status_parse_error", "Os DocumentType válidos" +
                    " são: [" + AUTHORIZED + "," + CREATED + ", " + CAPTURED + ", "+ CANCELLED +"]");
        }
    }

}
