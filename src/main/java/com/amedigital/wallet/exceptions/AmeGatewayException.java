package com.amedigital.wallet.exceptions;

import org.apache.http.HttpStatus;

public class AmeGatewayException extends AmeException {

    public AmeGatewayException() {
        super(HttpStatus.SC_INTERNAL_SERVER_ERROR, "gateway_communication_error", "Falha de comunicação com o gateway de pagamento.");
    }
}
