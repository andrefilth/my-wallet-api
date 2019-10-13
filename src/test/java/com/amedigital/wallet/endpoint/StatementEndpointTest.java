package com.amedigital.wallet.endpoint;


import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.amedigital.wallet.WalletApplication;
import com.amedigital.wallet.security.WalletToken;
import com.amedigital.wallet.service.impl.AuthorizationServiceImpl;
import com.b2wdigital.bpay.oauth2.token.ConsumerType;
import com.b2wdigital.bpay.oauth2.token.Token;

import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient(timeout = "36000")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {WalletApplication.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
public class StatementEndpointTest {

	@Autowired
	private WebTestClient webClient;
	
	@MockBean
	private AuthorizationServiceImpl authorizationService;
	
	@Test
	public void getStatementTest() throws Exception {
		
		Token token = new Token.Builder()
			.withUserId(UUID.randomUUID().toString())
			.withConsumerType(ConsumerType.USER)
			.withScopes(Collections.singleton("wallet:.*"))
			.withPayload(Collections.singletonMap("walletId", "invalidwallet"))
			.build();

		when(authorizationService.authorize(any(String.class))).thenReturn(Mono.just(new WalletToken(token)));
		
		webClient.get()
			.uri(WALLET_CONTEXT_PATH + "/statement")
			.header(AUTHORIZATION,  "Bearer foo")
			.exchange()
			.expectStatus().is4xxClientError()
		  ;
	}
}
