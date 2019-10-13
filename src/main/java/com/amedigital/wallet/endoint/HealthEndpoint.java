package com.amedigital.wallet.endoint;

import com.amedigital.wallet.endoint.response.HealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;

@RestController
public class HealthEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(HealthEndpoint.class);

    @GetMapping(WALLET_CONTEXT_PATH + "/health")
    public Mono<HealthResponse> health() {
        return Mono.just(new HealthResponse())
                .doOnSuccess(t -> LOG.info("Status da aplicação [{}]", t));
    }

}
