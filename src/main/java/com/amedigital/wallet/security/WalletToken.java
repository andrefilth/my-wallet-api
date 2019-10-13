package com.amedigital.wallet.security;

import com.b2wdigital.bpay.oauth2.token.Token;

import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class WalletToken {

    private static final String WALLET_ID_KEY = "walletId";
    private static final String CUSTOMER_ID_KEY = "customerId";
    private static final String MERCHANT_ID_KEY = "merchantId";

    private final Token token;

    public WalletToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }

    public Map<String, Object> getPayload() {
        return this.token.getPayload();
    }

    public String getClientId() {
        return this.token.getClientId();
    }

    public Optional<String> getWalletId() {

        if (this.getPayload() == null) {
            return Optional.empty();
        }

        if (!this.getPayload().containsKey(WALLET_ID_KEY)
                || this.getPayload().get(WALLET_ID_KEY) == null
                || isBlank(this.getPayload().get(WALLET_ID_KEY).toString())) {

            return Optional.empty();
        }

        return Optional.of(this.getPayload().get(WALLET_ID_KEY).toString());
    }

    public Optional<String> getCustomerId() {
        return Optional.of(this.getPayload().get(CUSTOMER_ID_KEY).toString());
    }

    public Optional<String> getMerchantId() {
        return Optional.of(this.getPayload().get(MERCHANT_ID_KEY).toString());
    }

}
