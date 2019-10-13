package com.amedigital.wallet.endpoint;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
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
import com.amedigital.wallet.endoint.request.method.BankTransferMethodRequest;
import com.amedigital.wallet.endoint.request.method.CashMethodRequest;
import com.amedigital.wallet.endoint.request.order.BankCashInOrderRequest;
import com.amedigital.wallet.endoint.request.order.StoreCashOutOrderRequest;
import com.amedigital.wallet.security.WalletToken;
import com.amedigital.wallet.service.fastcash.impl.FastCashServiceImpl;
import com.amedigital.wallet.service.fastcash.request.CashInRequest;
import com.amedigital.wallet.service.fastcash.response.CashInResponse;
import com.amedigital.wallet.service.impl.AuthorizationServiceImpl;
import com.b2wdigital.bpay.oauth2.token.ConsumerType;
import com.b2wdigital.bpay.oauth2.token.Token;

import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient(timeout = "3600000")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { WalletApplication.class })
@ActiveProfiles("test")
@TestPropertySource(properties = { "spring.main.allow-bean-definition-overriding=true" })
public class OrderEndpointTest {

	private static final String WALLET_UUID_TEST = "915918fc-0b28-48ed-88a2-5372413e19b5";

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private AuthorizationServiceImpl authorizationService;
	
	@MockBean
	private FastCashServiceImpl fastCashService;

	@Test
	void createAndAuthorizeBankCashInOrder() {

		Token token = new Token.Builder()
				.withUserId(UUID.randomUUID().toString())
				.withConsumerType(ConsumerType.USER)
				.withScopes(Collections.singleton("wallet:.*"))
				.withPayload(Collections.singletonMap("walletId", WALLET_UUID_TEST))
				.build();

		when(authorizationService.authorize(any(String.class))).thenReturn(Mono.just(new WalletToken(token)));
		
		CashInResponse cashInResponse = new CashInResponse();
		
		cashInResponse.setTid("bfb79fa9-4f1a-44b0-b70d-31cbc6e77315");
		cashInResponse.setDestinationAgency("6998-1");
		cashInResponse.setDestinationAccount("7496-9");
		cashInResponse.setDestinationAccountHolder("FC CORBAN LTDA");
		cashInResponse.setDestinationAccountHolderDocument("23.458.401/0001-95");
		
		when(fastCashService.requestCashInFromBankAccount(any(CashInRequest.class))).thenReturn(Mono.just(cashInResponse));
		

		BankTransferMethodRequest bankTransferMethodRequest = new BankTransferMethodRequest();

		bankTransferMethodRequest.setClientName("Foo Test");
		bankTransferMethodRequest.setClientCPF("28700241008");
		bankTransferMethodRequest.setClientEmail("fooEmail@foo.com");
		bankTransferMethodRequest.setClientPhone("12345678910");
		bankTransferMethodRequest.setBank(1);
		bankTransferMethodRequest.setAgency("0022");
		bankTransferMethodRequest.setAccountNumber("123456");
		bankTransferMethodRequest.setTaxApplied(0L);

		BankCashInOrderRequest bankCashInOrderRequest = new BankCashInOrderRequest();

		bankCashInOrderRequest.setPaymentMethods(Collections.singletonList(bankTransferMethodRequest));
		bankCashInOrderRequest.setCreatedByWalletId(WALLET_UUID_TEST);
		bankCashInOrderRequest.setDebitWalletId(WALLET_UUID_TEST);
		bankCashInOrderRequest.setTitle("Transferencia bancaria");
		bankCashInOrderRequest.setDescription("transferencia");
		bankCashInOrderRequest.setTotalAmountInCents(1234L);

		webClient.post()
			.uri(WALLET_CONTEXT_PATH + "/orders")
			.header(AUTHORIZATION, "Bearer foo")
			.accept(APPLICATION_JSON)
			.body(Mono.just(bankCashInOrderRequest), BankCashInOrderRequest.class)
			.exchange()
			.expectStatus()
			.is2xxSuccessful();
	}
	
	@Test
	@Disabled(value = "Adicionar credito na carteira para aprovar a ordem")
	void createAndAuthorizeStoreCashOutOrder() {
		
		Token token = new Token.Builder()
				.withUserId(UUID.randomUUID().toString())
				.withConsumerType(ConsumerType.USER)
				.withScopes(Collections.singleton("wallet:.*"))
				.withPayload(Collections.singletonMap("walletId", WALLET_UUID_TEST))
				.build();
		
		when(authorizationService.authorize(any(String.class))).thenReturn(Mono.just(new WalletToken(token)));
		
		CashMethodRequest cashMethodRequest = new CashMethodRequest();
		cashMethodRequest.setAmountInCents(1234L);
		
		StoreCashOutOrderRequest storeCashOutOrderRequest = new StoreCashOutOrderRequest();
		
		storeCashOutOrderRequest.setPaymentMethods(Collections.singletonList(cashMethodRequest));
		storeCashOutOrderRequest.setCreatedByWalletId(WALLET_UUID_TEST);
		storeCashOutOrderRequest.setDebitWalletId(WALLET_UUID_TEST);
		storeCashOutOrderRequest.setTitle("Store Cash Out");
		storeCashOutOrderRequest.setDescription("transferencia Store Cash Out ");
		storeCashOutOrderRequest.setTotalAmountInCents(1234L);
		
		webClient.post()
			.uri(WALLET_CONTEXT_PATH + "/orders")
			.header(AUTHORIZATION, "Bearer foo")
			.accept(APPLICATION_JSON)
			.body(Mono.just(storeCashOutOrderRequest), StoreCashOutOrderRequest.class)
			.exchange()
			.expectStatus()
			.is2xxSuccessful();
	}

}
