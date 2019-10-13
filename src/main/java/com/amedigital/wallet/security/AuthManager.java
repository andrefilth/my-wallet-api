package com.amedigital.wallet.security;

import com.amedigital.wallet.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthManager implements ReactiveAuthenticationManager {

    private final AuthorizationService authorizationService;

    @Autowired
    public AuthManager(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        TokenAuthentication auth = (TokenAuthentication) authentication;

        return Mono.just(auth)
                .map(TokenAuthentication::getBearerToken)
                .flatMap(authorizationService::authorize)
                .map(TokenAuthentication::new)
                .map(tokenAuthentication -> {
                    tokenAuthentication.setAuthenticated(true);
                    return tokenAuthentication;
                });
    }

}
