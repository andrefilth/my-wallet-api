package com.amedigital.wallet.constants;

import static java.lang.String.format;

public class Messages {

    public static String createdMessageException(final String status, final String orderType) {
        return format("Você não pode %s uma ordem de %s criada.", status, orderType);
    }

    public static String authorizedMessageException(final String status, final String orderType) {
        return format("Você não pode %s uma ordem de %s já autorizada.", status, orderType);
    }

    public static String capturedMessageException(final String status, final String orderType) {
        return format("Você não pode %s uma ordem de %s já capturada.", status, orderType);
    }

    public static String cancelledMessageException(final String status, final String orderType) {
        return format("Você não pode %s uma ordem de %s já cancelada.", status, orderType);
    }

    public static String deniedMessageException(final String status, final String orderType) {
        return format("Você não pode %s uma ordem de %s já negada.", status, orderType);
    }

    public static String releasedMessageException(final String status, final String orderType) {
        return format("Você não pode %s uma ordem de %s já liquidada.", status, orderType);
    }
}
