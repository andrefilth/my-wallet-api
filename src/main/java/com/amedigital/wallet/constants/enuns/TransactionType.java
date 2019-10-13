package com.amedigital.wallet.constants.enuns;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {
    DEBIT,
    CREDIT;

    @JsonCreator
    static TransactionType of(String value) {
        try {
            return valueOf(value);

        } catch (Exception e) {
            throw new AmeInvalidInputException("transaction_type_parse_error", "Os TransactionType válidos são: ["
                    + DEBIT + "," + CREDIT +  "]");
        }
    }

}
