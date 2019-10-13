package com.amedigital.wallet.security;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.b2wdigital.bpay.oauth2.token.ConsumerType;
import com.b2wdigital.bpay.oauth2.token.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

public class TokenAuthentication extends AbstractAuthenticationToken {

    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthentication.class);
    private String bearerToken;

    public TokenAuthentication(String bearerToken) {
        super(null);
        this.bearerToken = bearerToken;
    }

    public TokenAuthentication(WalletToken walletToken) {
        super(walletToken.getToken()
                .getScopes()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));

        validateToken(walletToken.getToken());
        this.setDetails(walletToken);
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public WalletToken getPrincipal() {
        return (WalletToken) this.getDetails();
    }

    private void validateToken(Token token) {
        var clientId = token.getClientId();

        if (ConsumerType.USER.equals(token.getConsumerType()) && (token.getPayload() == null || token.getPayload().isEmpty())) {
            LOG.error("Payload não encontrado no token com clientId [{}]", clientId);

            throw new AmeInvalidInputException("wallet_validation", "Payload não encontrado no token com clientId " + clientId + ".");
        }
    }
}
