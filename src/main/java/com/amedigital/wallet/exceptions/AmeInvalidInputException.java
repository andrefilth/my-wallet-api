package com.amedigital.wallet.exceptions;

import org.apache.http.HttpStatus;
import org.springframework.web.server.ServerWebInputException;

public class AmeInvalidInputException extends AmeException {

    public AmeInvalidInputException(ServerWebInputException ex) {
        super(HttpStatus.SC_BAD_REQUEST, "Entrada inválida", "Não foi possível parsear a entrada.");
    }

    public AmeInvalidInputException(String errorCode, String message) {
        super(HttpStatus.SC_BAD_REQUEST, errorCode, message);
    }

}
