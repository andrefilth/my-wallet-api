package com.amedigital.wallet.exceptions;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmeInternalException extends AmeException {

    private static final Logger LOG = LoggerFactory.getLogger(AmeInternalException.class);

    public AmeInternalException(String message) {
        super(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Ops... Ocorreu um erro interno.", message);
        LOG.error(message);
    }

    public AmeInternalException() {
        super(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Ops... Ocorreu um erro interno.", null);
    }

}
