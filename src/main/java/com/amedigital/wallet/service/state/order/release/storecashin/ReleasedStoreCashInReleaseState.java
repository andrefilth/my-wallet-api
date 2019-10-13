package com.amedigital.wallet.service.state.order.release.storecashin;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.StoreCashInOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.RELEASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.releasedMessageException;

@Service
public class ReleasedStoreCashInReleaseState implements SecondaryOrderState<StoreCashInOrder, ReleaseOrder> {

    @Autowired
    public ReleasedStoreCashInReleaseState() {
    }

    @Override
    public Mono<ReleaseOrder> create(StoreCashInOrder order, ReleaseOrder releaseOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                releasedMessageException("criar", RELEASE_ORDER_TYPE)));
    }

    @Override
    public Mono<ReleaseOrder> authorize(StoreCashInOrder order, ReleaseOrder releaseOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                releasedMessageException("autorizar", RELEASE_ORDER_TYPE)));
    }

    @Override
    public Mono<ReleaseOrder> finish(StoreCashInOrder order, ReleaseOrder releaseOrder) {
        return Mono.just(releaseOrder);
    }

	@Override
	public Mono<ReleaseOrder> cancel(StoreCashInOrder order, ReleaseOrder secondaryOrder) {
		return Mono.error(new AmeInvalidInputException("wallet_validation",
				releasedMessageException("cancelar", RELEASE_ORDER_TYPE)));
	}

}
