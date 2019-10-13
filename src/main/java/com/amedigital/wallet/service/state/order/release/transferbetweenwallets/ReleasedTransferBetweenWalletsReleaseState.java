package com.amedigital.wallet.service.state.order.release.transferbetweenwallets;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.RELEASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.releasedMessageException;

@Service
public class ReleasedTransferBetweenWalletsReleaseState implements SecondaryOrderState<TransferBetweenWalletsOrder, ReleaseOrder> {

    @Autowired
    public ReleasedTransferBetweenWalletsReleaseState() {
    }

    @Override
    public Mono<ReleaseOrder> create(TransferBetweenWalletsOrder order, ReleaseOrder releaseOrder) {
        throw new AmeInvalidInputException("wallet_validation",
                releasedMessageException("criar", RELEASE_ORDER_TYPE));
    }

    @Override
    public Mono<ReleaseOrder> authorize(TransferBetweenWalletsOrder order, ReleaseOrder releaseOrder) {
        throw new AmeInvalidInputException("wallet_validation",
                releasedMessageException("autorizar", RELEASE_ORDER_TYPE));
    }

    @Override
    public Mono<ReleaseOrder> finish(TransferBetweenWalletsOrder order, ReleaseOrder releaseOrder) {
        return Mono.just(releaseOrder);
    }

	@Override
	public Mono<ReleaseOrder> cancel(TransferBetweenWalletsOrder order, ReleaseOrder secondaryOrder) {
		throw new AmeInvalidInputException("wallet_validation",
                releasedMessageException("cancelar", RELEASE_ORDER_TYPE));
	}

}
