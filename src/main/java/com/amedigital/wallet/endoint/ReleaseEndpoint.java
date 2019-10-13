package com.amedigital.wallet.endoint;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.order.secondary.RouterSecondaryOrderRequestConverter;
import com.amedigital.wallet.endoint.request.order.ReleaseOrderRequest;
import com.amedigital.wallet.endoint.response.SecondaryOrderResponse;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.security.TokenAuthentication;
import com.amedigital.wallet.service.OrderService;
import com.amedigital.wallet.service.WalletService;
import com.amedigital.wallet.service.state.order.RouterSecondaryOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH;

@RestController
@RequestMapping(WALLET_CONTEXT_PATH)
public class ReleaseEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(ReleaseEndpoint.class);

    private final WalletService walletService;
    private final OrderService orderService;
    private final RouterSecondaryOrderRequestConverter converter;
    private final RouterSecondaryOrder secondaryOrderRouter;


    @Autowired
    public ReleaseEndpoint(WalletService walletService, OrderService orderService, RouterSecondaryOrderRequestConverter converter, RouterSecondaryOrder secondaryOrderRouter) {
        this.walletService = walletService;
        this.orderService = orderService;
        this.converter = converter;
        this.secondaryOrderRouter = secondaryOrderRouter;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/orders/{orderUuid}/release")
    public Mono<SecondaryOrderResponse> createAndAuthorize(@PathVariable("orderUuid") String orderUuid,
                                                           @RequestBody ReleaseOrderRequest request,
                                                           TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação de criação de liquidação recebida com orderUuid [{}]: [{}]", orderUuid, request);

        return createAndAuthorize(orderUuid, UUID.randomUUID().toString(), request, tokenAuthentication);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/orders/{orderUuid}/release/{releaseId}")
    public Mono<SecondaryOrderResponse> createAndAuthorize(@PathVariable("orderUuid") String orderUuid,
                                                           @PathVariable("releaseId") String releaseId,
                                                           @RequestBody ReleaseOrderRequest request,
                                                           TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação de criação de liquidação recebida com orderUuid [{}] e releaseId [{}]: [{}]", orderUuid, releaseId, request);

        return orderService.findByUuid(orderUuid)
                .flatMap(ord -> {
                    var walletId = tokenAuthentication.getPrincipal().getWalletId();
                    var walletMono = walletId.isPresent() ? walletService.findByUuid(walletId.get()) : walletService.findById(ord.getCreatedByWalletId());

                    return walletMono.map(wallet -> RequestContext.build().setTokenWallet(wallet)
                            .setPrimaryOrderUuid(orderUuid)
                            .setSecondaryId(releaseId)
                            .build())
                            .switchIfEmpty(Mono.error(new AmeException(404, "order_not_found", "Não foi possível encontrar a walletId da sessão do usuário")));
                })
                .flatMap(context ->
                        orderService.findByUuid(orderUuid)
                                .flatMap(order -> orderService.findByOrderReferenceAndSecondaryId(orderUuid, releaseId)
                                        .defaultIfEmpty((ReleaseOrder) converter.from(order, request, context)
                                                .copy()
                                                .setSecondaryId(releaseId)
                                                .build())
                                        .flatMap(releaseOrder -> secondaryOrderRouter.create(order, releaseOrder)))
                ).map(SecondaryOrderResponse::new)
                .doOnSuccess(wr -> LOG.info("Resposta da criação de liquidação", wr));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/orders/{orderUuid}/release/{secondaryOrderId}/{orderStatus}")
    public Mono<SecondaryOrderResponse> changeStatus(@PathVariable("orderUuid") String orderUuid,
                                                     @PathVariable("secondaryOrderId") String secondaryOrderId,
                                                     @PathVariable("orderStatus") OrderStatus orderStatus) {

        LOG.info("Solicitação de alteração de liquidação recebida com orderUuid [{}] e secondaryOrderId [{}]: [{}]", orderUuid, secondaryOrderId, orderStatus);

        return orderService.findByUuid(orderUuid)
                .switchIfEmpty(Mono.error(new AmeException(404, "order_not_found", "Não foi possível achar o pedido")))
                .flatMap(order -> orderService.findByOrderReferenceAndSecondaryId(orderUuid, secondaryOrderId)
                        .flatMap(releaseOrder -> secondaryOrderRouter.changeStatus(order, releaseOrder, orderStatus))
                        .switchIfEmpty(Mono.error(new AmeException(404, "order_not_found", "Não foi possível achar o pedido"))))
                .map(SecondaryOrderResponse::new)
                .doOnSuccess(wr -> LOG.info("Resposta de alteração de liquidação", wr));
    }

}