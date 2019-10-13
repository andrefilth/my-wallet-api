package com.amedigital.wallet.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AuthenticationWebFilter {

    @Autowired
    public AuthenticationFilter(ReactiveAuthenticationManager authManager,
                                ServerWebExchangeMatcher tokenMatcher,
                                AuthConverter authConverter) {
        super(authManager);
        setRequiresAuthenticationMatcher(tokenMatcher);
        setAuthenticationConverter(authConverter);
    }
}
