package com.amedigital.wallet.constants.enuns;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum WalletType {

    CUSTOMER,
    MERCHANT,
    MANAGER;

    @JsonCreator
    static WalletType of(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            throw new AmeInvalidInputException("wallet_type_parse_error", "Os WalletType válidos são: [" + MERCHANT + "," + CUSTOMER + "]");
        }
    }

}
