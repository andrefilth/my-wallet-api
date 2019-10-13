package com.amedigital.wallet.constants.enuns;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum DocumentType {
    CPF,
    CNPJ;

    @JsonCreator
    static DocumentType of(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            throw new AmeInvalidInputException("document_type_parse_error", "Os DocumentType válidos são: [" + CPF + "," + CNPJ  + "]");
        }
    }
}
