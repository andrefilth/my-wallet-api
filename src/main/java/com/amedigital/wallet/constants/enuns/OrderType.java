package com.amedigital.wallet.constants.enuns;

import java.util.Arrays;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderType {
    PURCHASE,
    CASH_IN,
    RELEASE,
    TRANSFER_BETWEEN_WALLETS,
    REFUND,
    CASH_BACK,
    GIFT_CASH_IN,
    STORE_CASH_IN,
    STORE_CASH_OUT,
    CASH_OUT,
    BANK_CASH_IN;

    @JsonCreator
    static OrderType of(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            throw new AmeInvalidInputException("order_type_parse_error", "Os OrderType válidos são: "
                    + Arrays.toString(OrderType.values()));
        }
    }
}
