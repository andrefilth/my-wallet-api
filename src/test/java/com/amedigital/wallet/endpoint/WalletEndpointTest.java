//package com.amedigital.wallet.endpoint;
//
//import com.amedigital.wallet.constants.enuns.DocumentType;
//import com.amedigital.wallet.constants.enuns.WalletType;
//import com.amedigital.wallet.endoint.request.OwnerRequest;
//import com.amedigital.wallet.endoint.request.WalletRequest;
//import com.amedigital.wallet.security.WalletToken;
//import com.amedigital.wallet.service.impl.AuthorizationServiceImpl;
//import com.b2wdigital.bpay.oauth2.token.ConsumerType;
//import com.b2wdigital.bpay.oauth2.token.Token;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Mono;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.UUID;
//
//import static com.amedigital.wallet.commons.IntegrationTest.uuid;
//import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;
//import static com.amedigital.wallet.constants.enuns.DocumentType.CNPJ;
//import static com.amedigital.wallet.constants.enuns.DocumentType.CPF;
//import static com.amedigital.wallet.constants.enuns.WalletType.CUSTOMER;
//import static com.amedigital.wallet.constants.enuns.WalletType.MERCHANT;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.http.HttpHeaders.AUTHORIZATION;
//
//@AutoConfigureWebTestClient(timeout = "36000")
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
//class WalletEndpointTest {
//
//    private final String BEARER_TOKEN = "Bearer " + uuid();
//    private final String WALLET_ID = "ed4f4c6c-fc5d-43f0-a878-5288613f8179";
//
//    @MockBean
//    private AuthorizationServiceImpl authorizationService;
//
//    @Autowired
//    private WebTestClient webClient;
//
//    @Nested
//    class FindWalletById {
//        @Test
//        @DisplayName("Deve realizar a pesquisa da wallet com o usuário autenticado")
//        void test1() {
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", WALLET_ID))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.get()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .exchange()
//                    .expectStatus().isOk()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .expectBody()
//                    .jsonPath("$.id").isNotEmpty()
//                    .jsonPath("$.id").isEqualTo(WALLET_ID)
//                    .jsonPath("$.name").isNotEmpty()
//                    .jsonPath("$.type").isNotEmpty()
//                    .jsonPath("$.main").isNotEmpty()
//                    .jsonPath("$.main").isBoolean()
//                    .jsonPath("$.createdAt").isNotEmpty()
//                    .jsonPath("$.updatedAt").isNotEmpty()
//                    .jsonPath("$.owner").isNotEmpty()
//                    .jsonPath("$.owner.id").isNotEmpty()
//                    .jsonPath("$.owner.id").isNotEmpty()
//                    .jsonPath("$.owner.name").isNotEmpty()
//                    .jsonPath("$.owner.email").isNotEmpty()
//                    .jsonPath("$.owner.document").isNotEmpty()
//                    .jsonPath("$.owner.documentType").isNotEmpty()
//                    .jsonPath("$.owner.createdAt").isNotEmpty()
//                    .jsonPath("$.owner.updatedAt").isNotEmpty();
//        }
//
//        @Test
//        @DisplayName("Deve retornar 404 quando não encontrar a carteira")
//        void test2() {
//            var walletId = "d4f865c3-53af-4c4d-8afa-d0f8e906d48e";
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", walletId))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.get()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .exchange()
//                    .expectStatus().isNotFound()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                    .expectBody()
//                    .jsonPath("$.error").isNotEmpty()
//                    .jsonPath("$.error").isEqualTo("wallet_validation")
//                    .jsonPath("$.error_description").isNotEmpty()
//                    .jsonPath("$.error_description").isEqualTo("Informações não encontradas com a walletId " + walletId);
//        }
//
//        @Test
//        @DisplayName("Deve retornar 400 quando o payload não possuir walletId")
//        void test3() {
//            Token token = new Token.Builder()
//                    .withClientId("1234")
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("teste", "1234"))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.get()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                    .expectBody()
//                    .jsonPath("$.error").isNotEmpty()
//                    .jsonPath("$.error").isEqualTo("wallet_validation")
//                    .jsonPath("$.error_description").isNotEmpty()
//                    .jsonPath("$.error_description").isEqualTo("ID da carteira não encontrada no payload do clientId 1234.");
//        }
//
//        @Test
//        @DisplayName("Deve retornar 400 quando o walletId for vazio")
//        void test4() {
//            Token token = new Token.Builder()
//                    .withClientId("1234")
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", ""))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.get()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                    .expectBody()
//                    .jsonPath("$.error").isNotEmpty()
//                    .jsonPath("$.error").isEqualTo("wallet_validation")
//                    .jsonPath("$.error_description").isNotEmpty()
//                    .jsonPath("$.error_description").isEqualTo("ID da carteira não encontrada no payload do clientId 1234.");
//        }
//
//        @Test
//        @DisplayName("Deve retornar 400 quando o walletId for nula")
//        void test5() {
//            Token token = new Token.Builder()
//                    .withClientId("1234")
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", null))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.get()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                    .expectBody()
//                    .jsonPath("$.error").isNotEmpty()
//                    .jsonPath("$.error").isEqualTo("wallet_validation")
//                    .jsonPath("$.error_description").isNotEmpty()
//                    .jsonPath("$.error_description").isEqualTo("ID da carteira não encontrada no payload do clientId 1234.");
//        }
//    }
//
//    @Nested
//    class TokenValidation {
//        @Test
//        @DisplayName("Deve retornar 400 quando o payload do token for nulo")
//        void test3() {
//            Token token = new Token.Builder()
//                    .withClientId("1234")
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(null)
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.get()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                    .expectBody()
//                    .jsonPath("$.error").isNotEmpty()
//                    .jsonPath("$.error").isEqualTo("wallet_validation")
//                    .jsonPath("$.error_description").isNotEmpty()
//                    .jsonPath("$.error_description").isEqualTo("Payload não encontrado no token com clientId 1234.");
//        }
//
//        @Test
//        @DisplayName("Deve retornar 400 quando o payload do token for vazio")
//        void test4() {
//            Token token = new Token.Builder()
//                    .withClientId("1234")
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(new HashMap<>())
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.get()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                    .expectBody()
//                    .jsonPath("$.error").isNotEmpty()
//                    .jsonPath("$.error").isEqualTo("wallet_validation")
//                    .jsonPath("$.error_description").isNotEmpty()
//                    .jsonPath("$.error_description").isEqualTo("Payload não encontrado no token com clientId 1234.");
//        }
//    }
//
//    @Nested
//    class CreateWalletTest {
//
//        OwnerRequest defaultOwner() {
//            var req = new OwnerRequest();
//            req.setDocumentType(CPF);
//            req.setDocument("00571945481");
//            req.setDocumentType(CPF);
//            req.setEmail("teste@amedigital.com");
//            req.setExternalId("1qaz2wx");
//            req.setName("Thiago Paixão");
//            return req;
//        }
//
//        WalletRequest defaultWallet() {
//            var req = new WalletRequest();
//            req.setMain(true);
//            req.setName("Carteira Principal");
//            req.setOwner(defaultOwner());
//            req.setType(CUSTOMER);
//            return req;
//        }
//
//        @Test
//        @DisplayName("Deve criar um CUSTOMER e uma WALLET")
//        void test1() {
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("customerId", UUID.randomUUID().toString()))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//
//            webClient.post()
//                    .uri(WALLET_CONTEXT_PATH + "/wallets")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(defaultWallet()), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isCreated()
//                    .expectBody()
//                    .jsonPath("$.id").isNotEmpty();
//        }
//
//        @Test
//        @DisplayName("Deve criar um MERCHANT e uma WALLET")
//        void test2() {
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("merchantId", UUID.randomUUID().toString()))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            var merchantWallet = defaultWallet();
//            merchantWallet.setType(MERCHANT);
//            merchantWallet.getOwner().setDocumentType(CNPJ);
//            merchantWallet.getOwner().setDocument("33014556000196");
//            merchantWallet.getOwner().setEmail("merchant@amedigital.com");
//
//
//            webClient.post()
//                    .uri(WALLET_CONTEXT_PATH + "/wallets")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(merchantWallet), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isCreated()
//                    .expectBody()
//                    .jsonPath("$.id").isNotEmpty();
//        }
//
//        @Test
//        @DisplayName("Deve falhar ao criar uma WALLET com um EMAIL já existente")
//        void test3() {
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("customerId", UUID.randomUUID().toString()))
//                    .build();
//
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            var ownerWallet = defaultWallet();
//            ownerWallet.getOwner().setEmail("morango.amigao@gmail.com");
//            ownerWallet.getOwner().setDocument("53131541253");
//
//            webClient.post()
//                    .uri(WALLET_CONTEXT_PATH + "/wallets")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(ownerWallet), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectBody()
//                    .jsonPath("$.error_description").isEqualTo("O email do owner já existe.");
//
//        }
//
//        @Test
//        @DisplayName("Deve falhar ao criar uma WALLET com um DOCUMENT já existente")
//        void test4() {
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("customerId", UUID.randomUUID().toString()))
//                    .build();
//
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            var ownerWallet = defaultWallet();
//            ownerWallet.getOwner().setEmail("angell.jolie@gmail.com");
//            ownerWallet.getOwner().setDocument("56922496000");
//
//            webClient.post()
//                    .uri(WALLET_CONTEXT_PATH + "/wallets")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(ownerWallet), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectBody()
//                    .jsonPath("$.error_description").isEqualTo("O documento do owner já existe.");
//        }
//
//        @Test
//        @DisplayName("Deve falhar ao criar uma WALLET com um 'documentType' CNPJ incompatível com o 'walletType' CUSTOMER")
//        void test5() {
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("customerId", UUID.randomUUID().toString()))
//                    .build();
//
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            var ownerWallet = defaultWallet();
//            ownerWallet.getOwner().setDocumentType(CNPJ);
//            ownerWallet.getOwner().setEmail("angelll.jolie@gmail.com");
//            ownerWallet.getOwner().setDocument("92421096812");
//
//            webClient.post()
//                    .uri(WALLET_CONTEXT_PATH + "/wallets")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(ownerWallet), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectBody()
//                    .jsonPath("$.error_description").isEqualTo("Formato inválido, um CUSTOMER não pode ter um CNPJ.");
//        }
//
//        @Test
//        @DisplayName("Deve falhar ao criar uma WALLET com um 'documentType' CPF incompatível com o 'walletType' MERCHANT")
//        void test6() {
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("customerId", UUID.randomUUID().toString()))
//                    .build();
//
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            var ownerWallet = defaultWallet();
//            ownerWallet.setType(MERCHANT);
//            ownerWallet.getOwner().setEmail("angelll.jolie@gmail.com");
//            ownerWallet.getOwner().setDocument("92421096812");
//
//            webClient.post()
//                    .uri(WALLET_CONTEXT_PATH + "/wallets")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(ownerWallet), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest();
//
//        }
//
//        @Test
//        @DisplayName("Deve falhar ao criar uma WALLET com um CPF inválido")
//        void test7() {
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("customerId", UUID.randomUUID().toString()))
//                    .build();
//
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            var ownerWallet = defaultWallet();
//            ownerWallet.getOwner().setEmail("angelll.jolie@gmail.com");
//            ownerWallet.getOwner().setDocument("123456789");
//
//            webClient.post()
//                    .uri(WALLET_CONTEXT_PATH + "/wallets")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(ownerWallet), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectBody()
//                    .jsonPath("$.error_description").isEqualTo("Formato de CPF inválido.");
//        }
//
//        @Test
//        @DisplayName("Deve falhar ao criar uma WALLET com um CNPJ inválido")
//        void test8() {
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("customerId", UUID.randomUUID().toString()))
//                    .build();
//
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            var ownerWallet = defaultWallet();
//            ownerWallet.setType(MERCHANT);
//            ownerWallet.getOwner().setDocumentType(CNPJ);
//            ownerWallet.getOwner().setEmail("angelll.jolie@gmail.com");
//            ownerWallet.getOwner().setDocument("123456789");
//
//            webClient.post()
//                    .uri(WALLET_CONTEXT_PATH + "/wallets")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(ownerWallet), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectBody()
//                    .jsonPath("$.error_description").isEqualTo("Formato de CNPJ inválido.");
//        }
//    }
//
//    @Nested
//    class UpdateWallet {
//
//        @Test
//        @DisplayName("Deve retornar exception quando os campos estiver nulos")
//        void test1() {
//            WalletRequest walletRequest = new WalletRequest();
//
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", WALLET_ID))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.put()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(walletRequest), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON);
//        }
//
//        @Test
//        @DisplayName("Deve retornar exception quando o campo tipo da carteira for nulo")
//        void test2() {
//            WalletRequest walletRequest = new WalletRequest();
//            walletRequest.setName("teste");
//
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", WALLET_ID))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.put()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(walletRequest), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON);
//        }
//
//        @Test
//        @DisplayName("Deve retornar exception quando já existir um email e documento cadastrado")
//        void test3() {
//            OwnerRequest ownerRequest = new OwnerRequest();
//            ownerRequest.setDocument("56922496000");
//            ownerRequest.setEmail("morango.amigao@gmail.com");
//            ownerRequest.setDocumentType(DocumentType.CPF);
//            ownerRequest.setExternalId("740ebe7a-e266-4c82-8887-db6e8109bb42");
//            ownerRequest.setName("teste 1");
//
//            WalletRequest walletRequest = new WalletRequest();
//            walletRequest.setName("teste");
//            walletRequest.setType(WalletType.CUSTOMER);
//            walletRequest.setOwner(ownerRequest);
//
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", WALLET_ID))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.put()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(walletRequest), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON);
//        }
//
//        @Test
//        @DisplayName("Deve retornar exception quando já existir um email cadastrado")
//        void test4() {
//            OwnerRequest ownerRequest = new OwnerRequest();
//            ownerRequest.setDocument("92421096812");
//            ownerRequest.setEmail("morango.amigao@gmail.com");
//            ownerRequest.setDocumentType(DocumentType.CPF);
//            ownerRequest.setExternalId("740ebe7a-e266-4c82-8887-db6e8109bb42");
//            ownerRequest.setName("teste 1");
//
//            WalletRequest walletRequest = new WalletRequest();
//            walletRequest.setName("teste");
//            walletRequest.setType(WalletType.CUSTOMER);
//            walletRequest.setOwner(ownerRequest);
//
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", WALLET_ID))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.put()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(walletRequest), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON);
//        }
//
//        @Test
//        @DisplayName("Deve retornar exception quando já existir um documento cadastrado")
//        void test5() {
//            OwnerRequest ownerRequest = new OwnerRequest();
//            ownerRequest.setDocument("56922496000");
//            ownerRequest.setEmail("morango.amigao2@gmail.com");
//            ownerRequest.setDocumentType(DocumentType.CPF);
//            ownerRequest.setExternalId("740ebe7a-e266-4c82-8887-db6e8109bb42");
//            ownerRequest.setName("teste 1");
//
//            WalletRequest walletRequest = new WalletRequest();
//            walletRequest.setName("teste");
//            walletRequest.setType(WalletType.CUSTOMER);
//            walletRequest.setOwner(ownerRequest);
//
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", WALLET_ID))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.put()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(walletRequest), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isBadRequest()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON);
//        }
//
//        @Test
//        @DisplayName("Deve alterar a wallet")
//        void test6() {
//            OwnerRequest ownerRequest = new OwnerRequest();
//            ownerRequest.setDocument("50403082056");
//            ownerRequest.setEmail("morango.amigao2@gmail.com");
//            ownerRequest.setDocumentType(DocumentType.CPF);
//            ownerRequest.setExternalId("740ebe7a-e266-4c82-8887-db6e8109bb42");
//            ownerRequest.setName("teste 1");
//
//            WalletRequest walletRequest = new WalletRequest();
//            walletRequest.setName("teste");
//            walletRequest.setType(WalletType.CUSTOMER);
//            walletRequest.setOwner(ownerRequest);
//
//            Token token = new Token.Builder()
//                    .withUserId(uuid())
//                    .withConsumerType(ConsumerType.USER)
//                    .withScopes(Collections.singleton("wallet:.*"))
//                    .withPayload(Collections.singletonMap("walletId", WALLET_ID))
//                    .build();
//
//            Mono<WalletToken> walletTokenMono = Mono.just(new WalletToken(token));
//
//            when(authorizationService.authorize(any(String.class))).thenReturn(walletTokenMono);
//
//            webClient.put()
//                    .uri(WALLET_CONTEXT_PATH + "/info")
//                    .header(AUTHORIZATION, BEARER_TOKEN)
//                    .body(Mono.just(walletRequest), WalletRequest.class)
//                    .exchange()
//                    .expectStatus().isOk()
//                    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .expectBody()
//                    .jsonPath("$.id").isNotEmpty()
//                    .jsonPath("$.id").isEqualTo(WALLET_ID)
//                    .jsonPath("$.name").isNotEmpty()
//                    .jsonPath("$.name").isEqualTo("teste")
//                    .jsonPath("$.type").isNotEmpty()
//                    .jsonPath("$.type").isEqualTo("CUSTOMER")
//                    .jsonPath("$.main").isNotEmpty()
//                    .jsonPath("$.main").isBoolean()
//                    .jsonPath("$.createdAt").isNotEmpty()
//                    .jsonPath("$.updatedAt").isNotEmpty()
//                    .jsonPath("$.owner").isNotEmpty()
//                    .jsonPath("$.owner.id").isNotEmpty()
//                    .jsonPath("$.owner.id").isEqualTo("b2e6d5e1-fe99-4ffe-9c7b-addf8c52d408")
//                    .jsonPath("$.owner.externalId").isNotEmpty()
//                    .jsonPath("$.owner.externalId").isEqualTo("740ebe7a-e266-4c82-8887-db6e8109bb42")
//                    .jsonPath("$.owner.name").isNotEmpty()
//                    .jsonPath("$.owner.name").isEqualTo("teste 1")
//                    .jsonPath("$.owner.email").isNotEmpty()
//                    .jsonPath("$.owner.email").isEqualTo("morango.amigao2@gmail.com")
//                    .jsonPath("$.owner.document").isNotEmpty()
//                    .jsonPath("$.owner.document").isEqualTo("50403082056")
//                    .jsonPath("$.owner.documentType").isNotEmpty()
//                    .jsonPath("$.owner.documentType").isEqualTo("CPF")
//                    .jsonPath("$.owner.createdAt").isNotEmpty()
//                    .jsonPath("$.owner.updatedAt").isNotEmpty();
//        }
//
//    }
//}
