package com.amedigital.wallet.endoint;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.order.secondary.RouterSecondaryOrderRequestConverter;
import com.amedigital.wallet.endoint.request.order.CashBackOrderRequest;
import com.amedigital.wallet.endoint.response.SecondaryOrderResponse;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
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
public class CashbackEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(CashbackEndpoint.class);

    private final WalletService walletService;
    private final OrderService orderService;
    private final RouterSecondaryOrderRequestConverter converter;
    private final RouterSecondaryOrder secondaryOrderRouter;


    @Autowired
    public CashbackEndpoint(WalletService walletService, OrderService orderService, RouterSecondaryOrderRequestConverter converter, RouterSecondaryOrder secondaryOrderRouter) {
        this.walletService = walletService;
        this.orderService = orderService;
        this.converter = converter;
        this.secondaryOrderRouter = secondaryOrderRouter;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/orders/{orderUuid}/cashback")
    public Mono<SecondaryOrderResponse> createAndAuthorize(@PathVariable("orderUuid") String orderUuid,
                                                           @RequestBody CashBackOrderRequest request,
                                                           TokenAuthentication tokenAuthentication) {
        return createAndAuthorize(orderUuid, UUID.randomUUID().toString(), request, tokenAuthentication);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/orders/{orderUuid}/cashback/{secondaryOrderId}")
    public Mono<SecondaryOrderResponse> createAndAuthorize(@PathVariable("orderUuid") String orderUuid,
                                                           @PathVariable("secondaryOrderId") String secondaryOrderId,
                                                           @RequestBody CashBackOrderRequest request,
                                                           TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação de criação de cashback recebida [{}]", request);

        return orderService.findByUuid(orderUuid)
                .switchIfEmpty(Mono.error(new AmeException(404, "order_not_found", "Não foi possível encontrar a order")))
                .flatMap(ord -> walletService.findById(ord.getCreatedByWalletId())
                        .switchIfEmpty(Mono.error(new AmeException(404, "wallet_not_found", "Não foi possível encontrar a walletId da sessão do merchant")))
                        .flatMap(wallet -> {
                            var context = RequestContext.build().setTokenWallet(wallet).setPrimaryOrderUuid(orderUuid).setSecondaryId(secondaryOrderId).build();
                            return orderService
                                    .findByOrderReferenceAndSecondaryId(orderUuid, secondaryOrderId)
                                    .defaultIfEmpty((CashbackOrder) converter.from(ord, request, context)
                                            .copy()
                                            .setSecondaryId(secondaryOrderId)
                                            .build())
                                    .flatMap(cbOrder -> secondaryOrderRouter.create(ord, cbOrder));
                        }))
                .map(SecondaryOrderResponse::new)
                .doOnSuccess(wr -> LOG.info("Resposta de criação de cashback: [{}].", wr));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/orders/{orderUuid}/cashback/{secondaryOrderId}/{orderStatus}")
    public Mono<SecondaryOrderResponse> changeStatus(@PathVariable("orderUuid") String orderUuid,
                                                     @PathVariable("secondaryOrderId") String secondaryOrderId,
                                                     @PathVariable("orderStatus") OrderStatus orderStatus) {

        LOG.info("Solicitação de [{}] de cashback com orderUuid [{}] e secondaryOrderUuid [{}] recebida", orderUuid, secondaryOrderId, orderStatus);

        return orderService.findByUuid(orderUuid)
                .switchIfEmpty(Mono.error(new AmeException(404, "order_not_found", "Não foi possível achar o pedido")))
                .flatMap(order -> orderService.findByOrderReferenceAndSecondaryId(orderUuid, secondaryOrderId)
                        .flatMap(releaseOrder -> secondaryOrderRouter.changeStatus(order, releaseOrder, orderStatus))
                        .switchIfEmpty(Mono.error(new AmeException(404, "order_not_found", "Não foi possível achar o pedido"))))
                .map(SecondaryOrderResponse::new)
                .doOnSuccess(wr -> LOG.info("Resposta de [{}] de cashback: [{}].", orderStatus, wr));
    }

}
