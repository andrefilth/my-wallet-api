package com.amedigital.wallet.constants.enuns;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum AuthorizationMethod {
    NONE,
    QRCODE,
    BARCODE;

    @JsonCreator
    static AuthorizationMethod of(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            throw new AmeInvalidInputException("authorization_method_parse_error", "Os AuthorizationMethod válidos são: ["
                    + NONE +  "," + QRCODE + "," + BARCODE + "]");
        }
    }

}
