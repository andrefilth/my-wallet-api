package com.amedigital.wallet.endoint.v3;

import com.amedigital.wallet.endoint.request.WalletRequest;
import com.amedigital.wallet.endoint.response.v3.WalletResponse;
import com.amedigital.wallet.endoint.response.v3.legacy.WResponse;
import com.amedigital.wallet.exceptions.AmeNotFoundException;
import com.amedigital.wallet.security.TokenAuthentication;
import com.amedigital.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH_V3;
import static com.amedigital.wallet.util.WalletUtil.getWalletId;

@RestController
@RequestMapping(WALLET_CONTEXT_PATH_V3)
public class WalletEndpointV3 {

    private static final Logger LOG = LoggerFactory.getLogger(WalletEndpointV3.class);


    private final WalletService service;

    @Autowired
    public WalletEndpointV3(WalletService service) {
        this.service = service;
    }

    @GetMapping("/info")
    public Mono<WalletResponse> findWallet(TokenAuthentication tokenAuthentication) {
        var walletId = getWalletId(tokenAuthentication);

        LOG.info("Solicitação de busca da wallet pela walletId [{}]", walletId);

        return service.findByUuid(walletId)
                .switchIfEmpty(Mono.error(new AmeNotFoundException("wallet_validation", "Informações não encontradas com a walletId " + walletId)))
                .map(WalletResponse::new)
                .doOnSuccess(wr -> LOG.info("Resposta da busca: wallet [{}] pela walletId [{}]", wr, walletId));
    }

    @PutMapping("/info")
    public Mono<WalletResponse> updateWallet(@RequestBody WalletRequest request,
                                             TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação de alteração da wallet [{}]", request);

        return service.update(request.toModel(), getWalletId(tokenAuthentication))
                .map(WalletResponse::new)
                .doOnSuccess(wr -> LOG.info("Resposta de alteração: wallet [{}]", wr));
    }

    @GetMapping("/wallets/{walletUuid}")
    public Mono<WResponse> findWalletByUuid(@PathVariable("walletUuid") String walletUuid) {

        LOG.info("Solicitação de busca pela walletUuid [{}]", walletUuid);

        return service.findByUuid(walletUuid)
                .switchIfEmpty(Mono.error(new AmeNotFoundException("wallet_validation", "Informações não encontradas com a walletId " + walletUuid)))
                .map(WResponse::new)
                .doOnSuccess(wr -> LOG.info("Resposta da busca: wallet [{}] pela walletId [{}]", wr, walletUuid));
    }

    @GetMapping("/wallets")
    public Mono<WalletResponse> findWalletByOwnerUuid(@RequestParam(value = "ownerId") String ownerUuid) {

        LOG.info("Solicitação de busca da wallet pelo ownerUuid [{}]", ownerUuid);

        return service.findByOwnerUuid(ownerUuid)
                .switchIfEmpty(Mono.error(new AmeNotFoundException("wallet_validation", "Informações não encontradas com o ownerId " + ownerUuid)))
                .map(WalletResponse::new)
                .doOnSuccess(wr -> LOG.info("Resposta de busca: wallet [{}] pelo ownerUuid [{}]", wr, ownerUuid));
    }


    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/wallets")
    public Mono<WalletResponse> createWallet(@RequestBody WalletRequest request) {

        LOG.info("Solicitação de criação da wallet [{}]", request);

        return service.create(request.toModel())
                .map(WalletResponse::new)
                .doOnSuccess(wr -> LOG.info("Resposta de criação da wallet [{}]", wr));
    }

}
