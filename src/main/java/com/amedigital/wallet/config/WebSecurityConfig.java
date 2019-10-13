package com.amedigital.wallet.config;

import com.amedigital.wallet.security.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final ReactiveAuthenticationManager authenticationManager;

    private final AuthenticationFilter authenticationFilter;

    @Autowired
    public WebSecurityConfig(ReactiveAuthenticationManager authenticationManager,
                             AuthenticationFilter authenticationFilter) {
        this.authenticationManager = authenticationManager;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .addFilterAt(this.authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .formLogin().disable()
                .csrf().disable()
                .httpBasic().disable()
                .logout().disable()
                .authenticationManager(this.authenticationManager)
                .build();
    }


}
