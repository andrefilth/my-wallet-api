package com.amedigital.wallet.exceptions;

import org.apache.http.HttpStatus;

public class AmeNotFoundException extends AmeException {

    public AmeNotFoundException() {
        super(HttpStatus.SC_NOT_FOUND, "resource_not_found", "Não foi possível encontrar o recurso");
    }

    public AmeNotFoundException(String errorCode, String message) {
        super(HttpStatus.SC_NOT_FOUND, errorCode, message);
    }
}
