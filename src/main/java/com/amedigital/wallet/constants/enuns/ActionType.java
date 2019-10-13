package com.amedigital.wallet.constants.enuns;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum ActionType {
    CREATE,
    AUTHORIZE,
    CAPTURE,
    CANCEL,
    REFUND,
    MIGRATION,
    RELEASE;

    @JsonCreator
    static ActionType of(String value) {
        try {
            return valueOf(value);

        } catch (Exception e) {
            throw new AmeInvalidInputException("action_type_parse_error", "Os ActionType válidos são: ["
                    + CREATE + "," + AUTHORIZE + ", " + CAPTURE + ", " + CANCEL + ", " +  ", " + RELEASE + "]");
        }
    }
}
