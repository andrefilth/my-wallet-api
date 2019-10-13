package com.amedigital.wallet.service.atom;

import com.amedigital.wallet.config.AtomConfig;
import com.amedigital.wallet.converters.CaptureConverter;
import com.amedigital.wallet.exceptions.AmeGatewayException;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.service.GatewayService;
import com.amedigital.wallet.service.atom.request.CancellationParameter;
import com.amedigital.wallet.service.atom.request.PaymentParameter;
import com.amedigital.wallet.service.atom.response.CancellationPresenter;
import com.amedigital.wallet.service.atom.response.OrderPresenter;
import com.amedigital.wallet.service.atom.response.TokenPresenter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class AtomService implements GatewayService {

    private static final String AUTHORIZE = "authorize";
    private static final String CAPTURE = "capture";
    private static final String CANCEL = "cancel";
    private static final String CANCELLATIONS = "cancellations";
    private static final String PAYMENTS = "payments";

    private final AtomConfig atomConfig;
    private final WebClient client;
    private final ObjectMapper objectMapper;
    private final Mono<TokenPresenter> atomToken;

    private final String AUTHORIZE_PAYMENT_URL;
    private final String HEADER_AUTHORIZATION_VALUE;
    private static final Logger LOG = LoggerFactory.getLogger(AtomService.class);

    @Autowired
    public AtomService(AtomConfig atomConfig, WebClient client, ObjectMapper objectMapper) {
        this.atomConfig = atomConfig;
        this.client = client;
        this.objectMapper = objectMapper;

        AUTHORIZE_PAYMENT_URL = String.format("%s/%s/%s", atomConfig.getUrl(), PAYMENTS, AUTHORIZE);
        HEADER_AUTHORIZATION_VALUE = "Basic " + Base64.getEncoder()
                .encodeToString(String.format("%s:%s", atomConfig.getClientId(), atomConfig.getClientSecret())
                        .getBytes(StandardCharsets.UTF_8));

        this.atomToken = this.getToken().cache(Duration.ofMinutes(30))
                .doOnSuccess(s -> LOG.info("Token do ATOM obtido com sucesso"));

    }

    @Override
    public Mono<OrderPresenter> authorization(PaymentParameter paymentParameter) {
        LOG.info("Iniciando a autorização da transação no ATOM [{}]", paymentParameter);

        return atomToken.flatMap(token -> client.post()
                .uri(AUTHORIZE_PAYMENT_URL)
                .header("X-AccountKey", atomConfig.getAccountKey())
                .header("Authorization", String.format("%s %s", token.getTokenType(), token.getAccessToken()))
                .accept(APPLICATION_JSON)
                .body(Mono.just(paymentParameter), PaymentParameter.class)
                .exchange()
                .timeout(Duration.ofSeconds(30))
                .flatMap(res -> res.bodyToMono(String.class)
                        .flatMap(s -> {
                            LOG.info("Resposta do ATOM na autorizacao, Status: [{}], URL: [{}], Response: [{}]]", res.statusCode(), AUTHORIZE_PAYMENT_URL, s);

                            if (res.statusCode().is2xxSuccessful()) {
                                return Mono.just(fromJson(s, OrderPresenter.class));
                            }

                            return Mono.error(new AmeGatewayException());
                        })));
    }

    @Override
    public Mono<CreditCardTransaction> capture(CreditCardTransaction transaction) {
        LOG.info("Iniciando a captura da transação no ATOM [{}]", transaction);

        return atomToken.flatMap(token -> client.put()
                .uri(String.format("%s/%s/%s/%s", atomConfig.getUrl(), PAYMENTS, transaction.getGatewayPaymentReference(), CAPTURE))
                .header("X-AccountKey", atomConfig.getAccountKey())
                .header("Authorization", String.format("%s %s", token.getTokenType(), token.getAccessToken()))
                .header("Content-Length", "0")
                .accept(APPLICATION_JSON)
                .exchange()
                .timeout(Duration.ofSeconds(30))
                .flatMap(res -> res.bodyToMono(String.class)
                        .flatMap(s -> {
                            LOG.info("Resposta do ATOM na captura, Status: [{}], URL: [{}], Response: [{}]]", res.statusCode(),
                                    String.format("%s/%s/%s/%s", atomConfig.getUrl(), PAYMENTS, transaction.getGatewayPaymentReference(), CAPTURE),
                                    s);

                            if (res.statusCode().is2xxSuccessful()) {
                                return Mono.just(fromJson(s, OrderPresenter.class))
                                        .map(e -> CaptureConverter.fromTransaction(transaction, e));

                            }

                            return Mono.just(CaptureConverter.defaultError(transaction));
                        })));
    }

    @Override
    public Mono<CancellationPresenter> cancellation(CancellationParameter cancellationParameter, String paymentReference) {
        LOG.info("Iniciando o cancelamento no ATOM [{}] com o paymentReference [{}]", cancellationParameter, paymentReference);

        return atomToken.flatMap(token -> client.put()
                .uri(String.format("%s/%s/%s/%s", atomConfig.getUrl(), PAYMENTS, paymentReference, CANCEL))
                .header("X-AccountKey", atomConfig.getAccountKey())
                .header("Authorization", String.format("%s %s", token.getTokenType(), token.getAccessToken()))
                .body(Mono.just(cancellationParameter), CancellationParameter.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .timeout(Duration.ofSeconds(30))
                .flatMap(res -> res.bodyToMono(String.class)
                        .flatMap(s -> {
                            LOG.info("Resposta do ATOM no cancelamento, Status: [{}], URL: [{}], Response: [{}]]", res.statusCode(),
                                    String.format("%s/%s/%s/%s", atomConfig.getUrl(), PAYMENTS, paymentReference, CANCEL),
                                    s);

                            if (res.statusCode().is2xxSuccessful()) {
                                return Mono.just(fromJson(s, CancellationPresenter.class));
                            }

                            return Mono.error(new AmeGatewayException());
                        }))
        );
    }

    @Override
    public Mono<CancellationPresenter> findByCancellationReference(final String cancellationReference) {
        LOG.info("Verificando o status de cancelamento no ATOM com o cancellationReference [{}]", cancellationReference);

        return atomToken.flatMap(token -> client.get()
                .uri(String.format("%s/%s/%s", atomConfig.getUrl(), CANCELLATIONS, cancellationReference))
                .header("X-AccountKey", atomConfig.getAccountKey())
                .header("Authorization", String.format("%s %s", token.getTokenType(), token.getAccessToken()))
                .exchange()
                .timeout(Duration.ofSeconds(30))
                .flatMap(res -> res.bodyToMono(String.class)
                        .flatMap(s -> {
                            LOG.info("Resposta do ATOM para pesquisar cancelamento, Status: [{}], URL: [{}], Response: [{}]]", res.statusCode(),
                                    String.format("%s/%s/%s", atomConfig.getUrl(), CANCELLATIONS, cancellationReference),
                                    s);

                            if (res.statusCode().is2xxSuccessful()) {
                                return Mono.just(fromJson(s, CancellationPresenter.class));
                            }

                            return Mono.error(new AmeGatewayException());
                        }))
        );
    }

    private Mono<TokenPresenter> getToken() {
        return client.post()
                .uri(atomConfig.getAuthUrl())
                .header("Authorization", HEADER_AUTHORIZATION_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .exchange()
                .timeout(Duration.ofSeconds(30))
                .flatMap(res -> {
                    if (res.statusCode().is2xxSuccessful()) {
                        return res.bodyToMono(TokenPresenter.class);

                    }

                    return Mono.error(new AmeGatewayException());
                });
    }

    private <T> T fromJson(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (IOException e) {
            LOG.error("Erro ao parsear json do ATOM:", e);
            throw new RuntimeException(e);
        }
    }
}
