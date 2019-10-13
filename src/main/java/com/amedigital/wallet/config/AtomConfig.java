package com.amedigital.wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtomConfig {

    @Value("${atom.url}")
    private String url;

    @Value("${atom.oauth2.url}")
    private String authUrl;

    @Value("${atom.oauth2.client.client-id}")
    private String clientId;

    @Value("${atom.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${atom.account-key}")
    private String accountKey;

    @Value("${atom.installment-type}")
    private String installmentType;

    @Value("${atom.business-unit}")
    private String businessUnit;

    @Value("${atom.soft-descriptor}")
    private String softDescriptor;

    public String getUrl() {
        return url;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public String getInstallmentType() {
        return installmentType;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public String getSoftDescriptor() {
        return softDescriptor;
    }
}
