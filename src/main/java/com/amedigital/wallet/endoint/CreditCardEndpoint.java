package com.amedigital.wallet.endoint;

import com.amedigital.wallet.endoint.request.CreditCardRequest;
import com.amedigital.wallet.endoint.response.CreditCardResponse;
import com.amedigital.wallet.exceptions.AmeNotFoundException;
import com.amedigital.wallet.security.TokenAuthentication;
import com.amedigital.wallet.service.CreditCardTokenService;
import com.amedigital.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;
import static com.amedigital.wallet.util.WalletUtil.getWalletId;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping(WALLET_CONTEXT_PATH)
public class CreditCardEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(CreditCardEndpoint.class);

    private final CreditCardTokenService creditCardTokenService;
    private final WalletService walletService;

    @Autowired
    public CreditCardEndpoint(CreditCardTokenService creditCardTokenService, WalletService walletService) {
        this.creditCardTokenService = creditCardTokenService;
        this.walletService = walletService;
    }

    @GetMapping("/creditcards")
    public Mono<ResponseEntity<List<CreditCardResponse>>> getCreditCard(TokenAuthentication tokenAuthentication) {
        return walletService.findByUuid(getWalletId(tokenAuthentication))
                .switchIfEmpty(Mono.error(new AmeNotFoundException()))
                .flatMap(wallet -> creditCardTokenService.findByWalletId(wallet.getId().get())
                        .map(CreditCardResponse::new)
                        .collectList()
                        .filter(x -> !x.isEmpty())
                        .map(ResponseEntity::ok)
                        .switchIfEmpty(Mono.just(noContent().build())));
    }

    @GetMapping("/creditcards/{id}")
    public Mono<ResponseEntity<CreditCardResponse>> getCardById(@PathVariable("id") String cardId,
                                                                TokenAuthentication token) {

        LOG.info("Solicitação para listagem de cartões com o cardId [{}] recebida", cardId);

        var walletId = getWalletId(token);
        return walletService.findByUuid(walletId)
                .switchIfEmpty(Mono.error(new AmeNotFoundException()))
                .flatMap(wallet -> creditCardTokenService.findCreditCardByUuid(cardId, wallet.getId().get()))
                .map(CreditCardResponse::new)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(notFound().build())
                .doOnSuccess(resp -> LOG.info("Resposta da listagem de cartão [{}] para a walletId [{}]",
                        cardId, walletId
                ));
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/creditcards")
    public Mono<CreditCardResponse> postCreditCard(@RequestBody CreditCardRequest request,
                                                   TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação para criação de cartão [{}] recebida", request);

        return walletService.findByUuid(getWalletId(tokenAuthentication))
                .switchIfEmpty(Mono.error(new AmeNotFoundException()))
                .flatMap(wallet -> creditCardTokenService.create(request.toModel(wallet.getId().get(), request.getId())))
                .map(CreditCardResponse::new)
                .doOnSuccess(creditCardResponse -> LOG.info("Resposta da criação de cartão de [{}] da walletId [{}]",
                        creditCardResponse, tokenAuthentication.getPrincipal().getWalletId()));
    }

    @PutMapping("/creditcards/{id}")
    public Mono<CreditCardResponse> putCreditCard(@PathVariable("id") String cardId,
                                                  @RequestBody CreditCardRequest request,
                                                  TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação para alteração de cartão [{}] com o cardUuid [{}] recebida", request, cardId);

        return walletService.findByUuid(getWalletId(tokenAuthentication))
                .switchIfEmpty(Mono.error(new AmeNotFoundException()))
                .flatMap(wallet -> creditCardTokenService.save(request.toModel(wallet.getId().get(), cardId)))
                .map(CreditCardResponse::new)
                .doOnSuccess(creditCardResponse -> LOG.info("Resposta da alteração de cartão de [{}] da walletId [{}]",
                        creditCardResponse, tokenAuthentication.getPrincipal().getWalletId()));
    }

    @DeleteMapping("/creditcards/{id}")
    public Mono<ResponseEntity<CreditCardResponse>> deleteCreditCard(@PathVariable("id") String cardId,
                                                                     TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação para deleção de cartão cardUuid [{}] recebida", cardId);

        var walletId = getWalletId(tokenAuthentication);
        return walletService.findByUuid(walletId)
                .switchIfEmpty(Mono.error(new AmeNotFoundException()))
                .flatMap(wallet -> creditCardTokenService.deleteCreditCardById(cardId, wallet.getId().get()))
                .map(CreditCardResponse::new)
                .map(x -> noContent().<CreditCardResponse>build())
                .doOnSuccess(creditCardResponse -> LOG.info("Cartão deletado com sucesso cardId [{}] e walletId [{}]", cardId, walletId));


    }
}
