package com.amedigital.wallet.endoint;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.order.primary.RouterPrimaryOrderRequestConverter;
import com.amedigital.wallet.endoint.request.order.OrderRequest;
import com.amedigital.wallet.endoint.response.OrderItemResponse;
import com.amedigital.wallet.endoint.response.OrderResponse;
import com.amedigital.wallet.endoint.response.PurchaseOrderWalletResponse;
import com.amedigital.wallet.endoint.response.TransferBetweenWalletsResponse;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.security.TokenAuthentication;
import com.amedigital.wallet.service.OrderService;
import com.amedigital.wallet.service.OwnerService;
import com.amedigital.wallet.service.WalletOperationAttributeService;
import com.amedigital.wallet.service.WalletService;
import com.amedigital.wallet.service.impl.order.RouterPrimaryOrderStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;

@RestController
@RequestMapping(WALLET_CONTEXT_PATH)
public class OrderEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(OrderEndpoint.class);

    private final RouterPrimaryOrderStateService service;
    private final WalletService walletService;
    private final OrderService orderService;
    private final RouterPrimaryOrderRequestConverter converter;
    private final OwnerService ownerService;
    private final WalletOperationAttributeService walletOperationAttributeService;

    @Autowired
    public OrderEndpoint(RouterPrimaryOrderStateService service,
                         WalletService walletService,
                         OrderService orderService,
                         RouterPrimaryOrderRequestConverter converter,
                         OwnerService ownerService,
                         WalletOperationAttributeService walletOperationAttribute) {

        this.service = service;
        this.walletService = walletService;
        this.orderService = orderService;
        this.converter = converter;
        this.ownerService = ownerService;
        this.walletOperationAttributeService = walletOperationAttribute;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/orders/{orderUuid}")
    public Mono<OrderResponse> findById(@PathVariable("orderUuid") String orderUuid) {

        LOG.info("Solicitação para buscar ordem com orderUuid [{}] recebida.", orderUuid);

        return orderService.findByUuid(orderUuid)
                .flatMap(order -> walletOperationAttributeService.findById(order.getOrderDetailUuid())
                        .map(walletOperationAttribute -> order.copy()
                                .setCustomPayload(walletOperationAttribute)
                                .build())
                        .switchIfEmpty(Mono.just(order.copy()
                                .setCustomPayload(Collections.emptyMap())
                                .build()))
                        .cast(Order.class)
                )
                .flatMap(order -> {
                    switch (order.getType()) {
                        case TRANSFER_BETWEEN_WALLETS:
                        case STORE_CASH_IN:
                            return Mono.justOrEmpty(order.getTransactions()
                                    .stream()
                                    .map(Transaction::getPeerWalletId)
                                    .findFirst())
                                    .flatMap(ownerService::findByWalletId)
                                    .map(owner -> new TransferBetweenWalletsResponse(order, owner));
                        case PURCHASE:
                            return Mono.justOrEmpty(order.getTransactions()
                                    .stream()
                                    .map(Transaction::getWalletId)
                                    .findFirst())
                                    .flatMap(ownerService::findByWalletId)
                                    .map(owner -> new PurchaseOrderWalletResponse(order, owner.getExternalId()));
                        case GIFT_CASH_IN:
                            return Mono.justOrEmpty(order.getTransactions()
                                    .stream()
                                    .map(Transaction::getWalletId)
                                    .findFirst())
                                    .flatMap(ownerService::findByWalletId)
                                    .map(owner -> new OrderResponse(order));
                        default:
                            return Mono.just(new OrderResponse(order));
                    }
                })
                .switchIfEmpty(Mono.error(new AmeException(404, "not_found", "Pedido não encontrado")))
                .doOnSuccess(orderResponse -> LOG.info("Resposta da pesquisa de ordem com o orderUuid [{}]: [{}]", orderUuid, orderResponse));
    }

    @GetMapping("/orders")
    public Mono<ResponseEntity<List<OrderItemResponse>>> findOrders(@RequestParam(value = "size", defaultValue = "20", required = false) Long size,
                                                                    @RequestParam(value = "offset", defaultValue = "0", required = false) Long offset,
                                                                    @RequestParam(value = "orderType", required = false) List<OrderType> orderTypes,
                                                                    @RequestParam(value = "referenceOrderId", required = false) String referenceOrderId,
                                                                    @RequestParam(value = "walletUuid", required = false) String walletUuid,
                                                                    @RequestParam(value = "cancellationReference", required = false) String cancellationReference,
                                                                    @RequestParam(value = "beginDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beginDate,
                                                                    @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                    TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação para listar ordens com os requestParam, size [{}]. offset [{}]. orderType [{}], " +
                "referenceOrderId [{}], walletUuid [{}], cancellationReference [{}]", size, offset, orderTypes, referenceOrderId, walletUuid, cancellationReference);

        return orderService.findBy(size, offset, orderTypes, Optional.ofNullable(referenceOrderId),
                Optional.ofNullable(walletUuid), Optional.ofNullable(cancellationReference), Optional.ofNullable(beginDate), Optional.ofNullable(endDate))
                .map(OrderItemResponse::new)
                .collectList()
                .map(orderItemResponses -> orderItemResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok().body(orderItemResponses));
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/orders")
    public Mono<OrderResponse> createAndAuthorize(@RequestBody OrderRequest request, TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação para criação de ordem recebida [{}]", request);

        return walletService.findByUuid(request.getDebitWalletId())
                .switchIfEmpty(Mono.error(new AmeInvalidInputException("wallet_not_found", "Não foi possível encontrar a wallet da sessão.")))
                .map(wallet -> RequestContext.build()
                        .setTokenWallet(wallet)
                        .build())
                .flatMap(context -> service.create(converter.from(request, context)))
                .map(OrderResponse::new)
                .doOnSuccess(order -> LOG.info("Resposta da criação de ordem [{}]", order));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/orders/{orderUuid}/status/{orderStatus}")
    public Mono<OrderResponse> changeStatus(@PathVariable("orderUuid") String orderUuid,
                                            @PathVariable("orderStatus") OrderStatus orderStatus,
                                            @RequestBody(required=false) Map<String, Object> bodyCustom) {

        LOG.info("Solicitação para alteração de status para [{}] da orderUuid [{}]", orderStatus, orderUuid);

        return service.changeStatus(orderUuid, orderStatus, bodyCustom)
                .map(OrderResponse::new)
                .doOnSuccess(r -> LOG.info("Resposta da alteração de status da ordem [{}]", r));
    }
}