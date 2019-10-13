package com.amedigital.wallet.service.impl.order;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.PrimaryOrder;
import com.amedigital.wallet.repository.DynamoRepository;
import com.amedigital.wallet.service.OrderService;
import com.amedigital.wallet.service.OrderStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class RouterPrimaryOrderStateService implements OrderStateService<PrimaryOrder> {

    private final OrderService orderService;
    private final Map<OrderType, OrderStateService> services = new HashMap<>();
    private final DynamoRepository dynamoRepository;

    @Autowired
    public RouterPrimaryOrderStateService(PurchaseOrderStateService purchaseOrderService,
                                          CashInOrderStateService cashInOrderService,
                                          TransferBetweenWalletsOrderStateService transferBetweenWalletsOrderService,
                                          OrderService orderService,
                                          GiftCashInOrderStateService giftCashInOrderStateService,
                                          StoreCashInOrderStateService storeCashInOrderStateService,
                                          StoreCashOutOrderStateService storeCashOutOrderStateService,
                                          CashOutOrderStateService cashOutOrderStateService,
                                          BankCashInOrderStateService bankCashInOrderStateService,
                                          DynamoRepository dynamoRepository) {

        this.orderService = orderService;
        this.dynamoRepository = dynamoRepository;

        services.put(OrderType.PURCHASE, purchaseOrderService);
        services.put(OrderType.TRANSFER_BETWEEN_WALLETS, transferBetweenWalletsOrderService);
        services.put(OrderType.CASH_IN, cashInOrderService);
        services.put(OrderType.GIFT_CASH_IN, giftCashInOrderStateService);
        services.put(OrderType.CASH_OUT, cashOutOrderStateService);
        services.put(OrderType.STORE_CASH_IN, storeCashInOrderStateService);
        services.put(OrderType.STORE_CASH_OUT, storeCashOutOrderStateService);
        services.put(OrderType.BANK_CASH_IN, bankCashInOrderStateService);
    }

    public Mono<PrimaryOrder> create(PrimaryOrder order) {
        return services.get(order.getType()).create(order);
    }

    @Override
    public Mono<PrimaryOrder> authorize(PrimaryOrder order) {
        return services.get(order.getType()).authorize(order);
    }

    @Override
    public Mono<PrimaryOrder> capture(PrimaryOrder order) {
        return services.get(order.getType()).capture(order);
    }

    @Override
    public Mono<PrimaryOrder> cancel(PrimaryOrder order) {
        return services.get(order.getType()).cancel(order);
    }

    public Mono<Order> changeStatus(String uuid, OrderStatus orderStatus, Map<String, Object> bodyCustom) {
        return orderService.findByUuid(uuid)
                .switchIfEmpty(Mono.error(new AmeException(404, "order_not_found", "Não foi possível achar o pedido")))
                .flatMap((order) -> dynamoRepository.findById(order.getOrderDetailUuid()).defaultIfEmpty(Collections.emptyMap())
                        .flatMap(o -> {
                            PrimaryOrder primaryOrder = (PrimaryOrder) order;

                            if (bodyCustom != null) {
                                if (!o.isEmpty()) {
                                    o.putAll(bodyCustom);
                                } else {
                                    o = bodyCustom;
                                }
                            }
                            
                            var or = (PrimaryOrder) primaryOrder.copy()
                                    .setCustomPayload(o)
                                    .build();

                            switch (orderStatus) {
                                case AUTHORIZED:
                                    return authorize(or);
                                case CAPTURED:
                                    return capture(or);
                                case CANCELLED:
                                    return cancel(or);
                                default:
                                    return Mono.error(new AmeInvalidInputException("unsupported_order_status", "O status da ordem não é suportado"));
                            }
                        }));

    }
}
