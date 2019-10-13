package com.amedigital.wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FastCashConfig {

    @Value("${fastcash.url}")
    private String url;
    
    public String getUrl() {
        return url;
    }
}
