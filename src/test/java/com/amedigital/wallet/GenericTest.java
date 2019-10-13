package com.amedigital.wallet;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.TestPropertySource;

import com.b2wdigital.bpay.client.service.RestTemplateService;
import com.b2wdigital.bpay.oauth2.response.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class GenericTest {

	protected static TokenResponse TOKEN;
	
	@BeforeAll
	public static void setUp() {
		
		//TOKEN = generateToken("22809993823", "Ame@1234");
	}

	@AfterAll
	public static void finish() {
	}
	
    protected static TokenResponse generateToken(String username, String password) {
    	
        String credential = String.format("%s:%s", "0fe9a275-a269-4033-a6e3-88c97aa6ea3d", "bpay@123");
        String encodedCredential = Base64.getEncoder().encodeToString(credential.getBytes(StandardCharsets.UTF_8));

        Headers.Builder headers = new Headers.Builder();

        headers.add(AUTHORIZATION, "Basic " + encodedCredential);

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("grant_type", "password")
                .build();

        String platformsBaseUri = "http://api.dev.amedigital.com/auth/v1/platforms/bpay/oauth/token";
		Request authorizationRequest = new Request.Builder()
                .url(platformsBaseUri)
                .headers(headers.build())
                .post(requestBody)
                .build();

        return new RestTemplateService(new OkHttpClient(), new ObjectMapper()).execute(authorizationRequest, TokenResponse.class);
    }
}
