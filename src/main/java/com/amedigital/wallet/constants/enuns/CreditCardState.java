package com.amedigital.wallet.constants.enuns;

public enum CreditCardState {

    AVAILABLE,
    EXPIRED;

    /**
     * Converte a String para enum
     *
     * @param name nome a ser convertido para enum
     * @return constante enum com o nome especificado ou {@code null} caso {@code name} seja null
     * @throws IllegalArgumentException - caso o enum não tenha a constante com o nome especificado
     */
    public static CreditCardState of(String name) {
        return name != null ? valueOf(name) : null;
    }


}
