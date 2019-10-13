/*
TODO: REMOVER ESSE FLUXO APOS AJUSTAR A INTEGRAÇÃO COM O ANTIFRAUDE.
 */
package com.amedigital.wallet.endoint.provisorio;

import com.amedigital.wallet.endoint.response.SimpleCreditCardResponse;
import com.amedigital.wallet.repository.CreditCardTokenRepository;
import com.amedigital.wallet.security.TokenAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;

@RestController
@RequestMapping(WALLET_CONTEXT_PATH)
public class DecodeCardEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(DecodeCardEndpoint.class);

    private final CreditCardTokenRepository repository;

    @Autowired
    public DecodeCardEndpoint(CreditCardTokenRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/opencard/{orderUuid}")
    public Mono<SimpleCreditCardResponse> getCardsByOrder(@PathVariable("orderUuid") String orderUuid, TokenAuthentication tokenAuthentication) {
        LOG.info("Buscando cartões para antifraude da ordem [{}]", orderUuid);

        return repository.findByOrderUuid(orderUuid).doOnSuccess(s -> LOG.info("Retornando informações do cartão: [{}]", s));
    }
}
