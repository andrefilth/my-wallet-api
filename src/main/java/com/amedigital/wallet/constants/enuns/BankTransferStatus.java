package com.amedigital.wallet.constants.enuns;

import java.util.Arrays;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum BankTransferStatus {
    CREATED,
    AUTHORIZED,
    DENIED,
    CAPTURED,
    ERROR,
    CANCELLED,
    PENDING;


    @JsonCreator
    static BankTransferStatus of(String value) {
        try {
            return valueOf(value);
        } catch (Exception e) {
            throw new AmeInvalidInputException("banktransfer_status_parse_error", "Os Types validos sao: " 
            		+ Arrays.toString(BankTransferStatus.values()));
        }
    }

}
