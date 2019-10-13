package com.amedigital.wallet.endoint.v3;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.endoint.converter.order.primary.RouterPrimaryOrderRequestConverter;
import com.amedigital.wallet.endoint.response.v3.query.SimpleReleaseStatementResponse;
import com.amedigital.wallet.security.TokenAuthentication;
import com.amedigital.wallet.service.OrderService;
import com.amedigital.wallet.service.OwnerService;
import com.amedigital.wallet.service.WalletOperationAttributeService;
import com.amedigital.wallet.service.WalletService;
import com.amedigital.wallet.service.impl.order.RouterPrimaryOrderStateService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

import static com.amedigital.wallet.constants.Constants.WALLET_CONTEXT_PATH_V3;

@RestController
@RequestMapping(WALLET_CONTEXT_PATH_V3)
public class OrderEndpointV3 {

    private static final Logger LOG = LoggerFactory.getLogger(com.amedigital.wallet.endoint.OrderEndpoint.class);
    private final RouterPrimaryOrderStateService service;
    private final WalletService walletService;
    private final OrderService orderService;
    private final RouterPrimaryOrderRequestConverter converter;
    private final OwnerService ownerService;
    private final WalletOperationAttributeService walletOperationAttributeService;

    @Autowired
    public OrderEndpointV3(RouterPrimaryOrderStateService service, WalletService walletService, OrderService orderService, RouterPrimaryOrderRequestConverter converter, OwnerService ownerService, WalletOperationAttributeService walletOperationAttributeService) {
        this.service = service;
        this.walletService = walletService;
        this.orderService = orderService;
        this.converter = converter;
        this.ownerService = ownerService;
        this.walletOperationAttributeService = walletOperationAttributeService;
    }

    @GetMapping("/orders")
    public Flux<SimpleReleaseStatementResponse> findSecondaryOrders(
            @RequestParam(value = "beginDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beginDate,
            @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "orderStatus", required = false) List<OrderStatus> orderStatus,
            TokenAuthentication tokenAuthentication) {

        LOG.info("Solicitação para listar ordens na API v3 com os requestParam: " +
                "beginDate [{}], endDate [{}]", beginDate, endDate);

        return orderService.findReleaseStatementBy(orderStatus, Lists.newArrayList(OrderType.RELEASE), beginDate, endDate);

    }


}
