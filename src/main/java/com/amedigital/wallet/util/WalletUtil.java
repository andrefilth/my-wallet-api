package com.amedigital.wallet.util;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.security.TokenAuthentication;

public class WalletUtil {

    public static String getWalletId(TokenAuthentication tokenAuthentication) {
        return tokenAuthentication.getPrincipal()
                .getWalletId()
                .orElseThrow(() -> new AmeInvalidInputException("wallet_validation",
                        "ID da carteira não encontrada no payload do clientId " + tokenAuthentication.getPrincipal().getClientId() + "."));
    }

}
