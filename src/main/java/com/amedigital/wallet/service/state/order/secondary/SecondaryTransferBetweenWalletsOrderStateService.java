package com.amedigital.wallet.service.state.order.secondary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.impl.order.cashback.CashbackTransferBetweenWalletOrderStateService;
import com.amedigital.wallet.service.impl.order.release.ReleaseStoreCashInOrderStateService;
import com.amedigital.wallet.service.impl.order.release.ReleaseTransferBetweenWalletOrderStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecondaryTransferBetweenWalletsOrderStateService  implements SecondaryOrderStateService<TransferBetweenWalletsOrder, SecondaryOrder> {

    private final Map<OrderType, SecondaryOrderStateService> services = new HashMap<>();

    @Autowired
    public SecondaryTransferBetweenWalletsOrderStateService(ReleaseTransferBetweenWalletOrderStateService releaseTransferBetweenWalletOrderStateService,
                                                            CashbackTransferBetweenWalletOrderStateService cashbackTransferBetweenWalletOrderStateService) {
        services.put(OrderType.RELEASE, releaseTransferBetweenWalletOrderStateService);
        services.put(OrderType.CASH_BACK, cashbackTransferBetweenWalletOrderStateService);
    }

    @Override
    public Mono<SecondaryOrder> create(TransferBetweenWalletsOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).create(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> authorize(TransferBetweenWalletsOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).authorize(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> finish(TransferBetweenWalletsOrder order, SecondaryOrder secondaryOrder) {
        return services.get(secondaryOrder.getType()).finish(order, secondaryOrder);
    }

    @Override
    public Mono<SecondaryOrder> cancel(TransferBetweenWalletsOrder order, SecondaryOrder secondaryOrder) {
    	return services.get(secondaryOrder.getType()).cancel(order, secondaryOrder);
    }



}
