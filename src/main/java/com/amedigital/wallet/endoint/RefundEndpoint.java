package com.amedigital.wallet.endoint;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.order.secondary.RouterSecondaryOrderRequestConverter;
import com.amedigital.wallet.endoint.request.order.RefundOrderRequest;
import com.amedigital.wallet.endoint.response.SecondaryOrderResponse;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
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
public class RefundEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(RefundEndpoint.class);

    private final WalletService walletService;
    private final OrderService orderService;
    private final RouterSecondaryOrder secondaryRouter;
    private final RouterSecondaryOrderRequestConverter converter;

    @Autowired
    public RefundEndpoint(WalletService walletService, OrderService orderService, RouterSecondaryOrder secondaryRouter, RouterSecondaryOrderRequestConverter converter) {
        this.walletService = walletService;
        this.orderService = orderService;
        this.secondaryRouter = secondaryRouter;
        this.converter = converter;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/orders/{orderUuid}/refunds")
    public Mono<SecondaryOrderResponse> createAndAuthorize(@PathVariable("orderUuid") String orderUuid,
                                                           @RequestBody RefundOrderRequest request,
                                                           TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação de estorno recebida com o orderUuid [{}]: [{}]", orderUuid, request);

        return createAndAuthorize(orderUuid, UUID.randomUUID().toString(), request, tokenAuthentication);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/orders/{orderUuid}/refunds/{secondaryOrderId}")
    public Mono<SecondaryOrderResponse> createAndAuthorize(@PathVariable("orderUuid") String orderUuid,
                                                           @PathVariable("secondaryOrderId") String secondaryOrderId,
                                                           @RequestBody RefundOrderRequest refundOrderRequest,
                                                           TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação de estorno recebida com o orderUuid [{}] e secondaryId [{}]: [{}]", orderUuid, secondaryOrderId, refundOrderRequest);

        var walletUuid = tokenAuthentication.getPrincipal()
                .getWalletId()
                .orElseThrow(() -> new AmeException(404, "wallet_not_found", "Não foi possível encontrar a walletId da sessão do usuário."));

        return walletService.findByUuid(walletUuid)
                .map(wallet -> RequestContext.build().setTokenWallet(wallet)
                        .setPrimaryOrderUuid(orderUuid)
                        .setSecondaryId(secondaryOrderId)
                        .build())
                .switchIfEmpty(Mono.error(new AmeException(404, "order_not_found", "Não foi possível encontrar a walletId da sessão do usuário")))
                .flatMap(context -> findOrder(orderUuid, secondaryOrderId, refundOrderRequest, context))
                .map(SecondaryOrderResponse::new)
                .doOnSuccess(refundOrder -> LOG.info("Resposta da criação de estorno", refundOrder));
    }

    private Mono<SecondaryOrder> findOrder(final String orderUuid,
                                           final String refundId,
                                           final RefundOrderRequest refundOrderRequest,
                                           final RequestContext context) {

        return orderService.findByUuid(orderUuid)
                .flatMap(order -> createRefundOrder(orderUuid, refundId, refundOrderRequest, context, order))
                .switchIfEmpty(Mono.error(new AmeException(404, "order_not_found", "Não foi possível encontrar a ordem referenciada.")));
    }

    private Mono<SecondaryOrder> createRefundOrder(final String orderUuid,
                                                   final String refundId,
                                                   final RefundOrderRequest refundOrderRequest,
                                                   final RequestContext context, Order order) {

        return orderService.findByOrderReferenceAndSecondaryId(orderUuid, refundId)
                .filter(o -> o.getSecondaryId().equals(refundId))
                .defaultIfEmpty((SecondaryOrder) converter.from(order, refundOrderRequest, context)
                        .copy()
                        .setSecondaryId(refundId)
                        .build())
                .flatMap(secondaryOrder -> secondaryRouter.changeStatus(order, secondaryOrder, OrderStatus.REFUNDED));
    }

}