package com.amedigital.wallet.endoint;

import com.amedigital.wallet.security.TokenAuthentication;
import com.amedigital.wallet.service.WalletOperationAttributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;

@RestController
@RequestMapping(WALLET_CONTEXT_PATH)
public class CustomPayloadEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(CustomPayloadEndpoint.class);

    private final WalletOperationAttributeService service;

    @Autowired
    public CustomPayloadEndpoint(WalletOperationAttributeService service) {
        this.service = service;
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PostMapping("/customPayloads/{orderDetailUuid}")
    public Mono<Map<String, Object>> saveCustomPayload(@PathVariable("orderDetailUuid") String orderDetailUuid,
                                                       @RequestBody Map<String, Object> customPayload,
                                                       TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação para salvar customPayload para o orderDetailUuid [{}]", orderDetailUuid);

        return service.save(orderDetailUuid, customPayload)
                .flatMap(cp -> Mono.empty());
    }

}
