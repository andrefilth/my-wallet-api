package com.amedigital.wallet.service.atom.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Set;

public class TokenPresenter {

    @JsonProperty(value = "access_token")
    private String accessToken = "1da68746-87c9-487f-bd7c-13f1ab2c66dd";

    @JsonProperty(value = "token_type")
    private String tokenType = "Bearer";

    @JsonProperty(value = "expires_in")
    private long expiresIn = 3599;

    @JsonProperty(value = "refresh_token")
    private String refreshToken = "c4fc088d-7e5f-481b-804f-932135a78a5a";

    private Map<String, Object> payload;

    private Set<String> scope;

    @JsonProperty("jti")
    private String tokenUniqueIdentifier;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public String getTokenUniqueIdentifier() {
        return tokenUniqueIdentifier;
    }

    public void setTokenUniqueIdentifier(String tokenUniqueIdentifier) {
        this.tokenUniqueIdentifier = tokenUniqueIdentifier;
    }
}
