package com.amedigital.wallet.endoint;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.endoint.request.TransactionDataRequest;
import com.amedigital.wallet.endoint.response.StatementItemResponse;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.TransactionDataItem;
import com.amedigital.wallet.security.TokenAuthentication;
import com.amedigital.wallet.service.StatementService;
import com.amedigital.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static com.amedigital.wallet.constants.Constants.INTERNATIONAL_DATE_FORMAT;
import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;
import static java.util.Collections.emptyList;

@RestController
@RequestMapping(WALLET_CONTEXT_PATH)
public class StatementEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(StatementEndpoint.class);

    private final StatementService statementService;
    private final WalletService walletService;

    @Autowired
    public StatementEndpoint(StatementService statementService, WalletService walletService) {
        this.statementService = statementService;
        this.walletService = walletService;
    }

    @GetMapping("/statement")
    public Mono<ResponseEntity<List<StatementItemResponse>>> getStatement(@RequestParam(value = "size", defaultValue = "20", required = false) Integer size,
                                                                          @RequestParam(value = "offset", defaultValue = "0", required = false) Integer offset,
                                                                          @RequestParam(value = "transactionType", required = false) List<TransactionType> transactionTypes,
                                                                          @RequestParam(value = "orderStatus", required = false) List<OrderStatus> ordersStatus,
                                                                          TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação para listagem de extrato com size [{}], offset [{}], transactionType [{}] e orderStatus [{}]",
                size, offset, transactionTypes, ordersStatus);

        return walletService.findByUuid(tokenAuthentication.getPrincipal().getWalletId().get())
                .switchIfEmpty(Mono.error(new AmeInvalidInputException("wallet_not_found", "Não foi possível encontrar a wallet da sessão.")))
                .flatMap(wallet -> statementService.getWalletStatement(wallet.getId().get(), size, offset,
                        Optional.ofNullable(transactionTypes).orElse(emptyList()),
                        Optional.ofNullable(ordersStatus).orElse(emptyList()))
                        .map(StatementItemResponse::new)
                        .collectList()
                        .map(ResponseEntity::ok)
                        .defaultIfEmpty(ResponseEntity.noContent().build()));
    }

    @GetMapping("/transactions/data/{walletId}/type/{orderType}/status/{orderStatus}/daterange/{dateStart}/{dateEnd}/")
    public Mono<ResponseEntity<List<TransactionDataItem>>> transactionData(
            @PathVariable(value = "walletId", required = false) String walletId,
            @PathVariable(value = "orderType", required = false) OrderType orderType,
            @PathVariable(value = "orderStatus", required = false) OrderStatus orderStatus,
            @PathVariable(value = "dateStart", required = false) String dateStart,
            @PathVariable(value = "dateEnd", required = false) String dateEnd) {
        var transactionDataRequest = TransactionDataRequest.builder()
                .setWalletId(walletId)
                .setStatus(orderStatus)
                .setType(orderType)
                .setDateStart(LocalDate.parse(dateStart, INTERNATIONAL_DATE_FORMAT).atStartOfDay(ZoneOffset.UTC))
                .setDateEnd(LocalDate.parse(dateEnd, INTERNATIONAL_DATE_FORMAT).atStartOfDay(ZoneOffset.UTC))
                .build();

        LOG.info("Iniciando pesquisa pelos critérios [{}]", transactionDataRequest);

        return Mono.just(transactionDataRequest)
                .flatMapMany(statementService::transactionData)
                .collectList()
                .map(ResponseEntity::ok)
                .doOnSuccess(s -> LOG.info("Dados retornados foram: [{}]", s))
                .doOnError(e -> LOG.error("Ocorreu um erro", e))
                .defaultIfEmpty(ResponseEntity.noContent().build());

    }

}
