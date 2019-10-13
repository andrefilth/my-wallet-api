package com.amedigital.wallet.endpoint;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.amedigital.wallet.WalletApplication;

@AutoConfigureWebTestClient(timeout = "36000")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {WalletApplication.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
public class HealthEndpointTest {
	
	@Autowired
	private WebTestClient webClient;
	
	@Test
	void helthCheckTest() throws Exception {
		
		webClient.get()
			.uri(WALLET_CONTEXT_PATH + "/health")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.status").isEqualTo("OK");
	}
}
