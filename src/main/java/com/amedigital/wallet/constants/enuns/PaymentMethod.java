package com.amedigital.wallet.constants.enuns;

import java.util.Arrays;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    CREDIT_CARD,
    CASH,
	BANK_TRANSFER,
    CASH_BACK;

    @JsonCreator
    static PaymentMethod of(String value) {
        try {
            return valueOf(value);

        } catch (Exception e) {
            throw new AmeInvalidInputException("payment_type_parse_error", "Os PaymentMethodService válidos são: "
                    + Arrays.toString(PaymentMethod.values()));
        }
    }
}
