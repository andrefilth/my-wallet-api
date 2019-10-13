package com.amedigital.wallet.constants.enuns;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderStatus {
    CREATED,
    AUTHORIZED,
    DENIED,
    CAPTURED,
    CANCELLED,
    PENDING,
    REFUNDED,
    RELEASED;

    @JsonCreator
    static OrderStatus of(String value) {
        try {
            return valueOf(value);

        } catch (Exception e) {
            throw new AmeInvalidInputException("order_status_parse_error", "Os OrderStatus válidos são: ["
                    + CREATED + "," + AUTHORIZED + ", " + CAPTURED + ", " + CANCELLED + ", " + RELEASED + "]");
        }
    }
}
