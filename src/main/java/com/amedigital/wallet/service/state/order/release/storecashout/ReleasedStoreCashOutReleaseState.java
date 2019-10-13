package com.amedigital.wallet.service.state.order.release.storecashout;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.amedigital.wallet.constants.Constants.RELEASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.releasedMessageException;

@Service
public class ReleasedStoreCashOutReleaseState implements SecondaryOrderState<StoreCashOutOrder, ReleaseOrder> {

    @Autowired
    public ReleasedStoreCashOutReleaseState() {
    }

    @Override
    public Mono<ReleaseOrder> create(StoreCashOutOrder order, ReleaseOrder releaseOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                releasedMessageException("criar", RELEASE_ORDER_TYPE)));
    }

    @Override
    public Mono<ReleaseOrder> authorize(StoreCashOutOrder order, ReleaseOrder releaseOrder) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                releasedMessageException("autorizar", RELEASE_ORDER_TYPE)));
    }

    @Override
    public Mono<ReleaseOrder> finish(StoreCashOutOrder order, ReleaseOrder releaseOrder) {
        return Mono.just(releaseOrder);
    }

	@Override
	public Mono<ReleaseOrder> cancel(StoreCashOutOrder order, ReleaseOrder secondaryOrder) {
		return Mono.error(new AmeInvalidInputException("wallet_validation",
				releasedMessageException("cancelar", RELEASE_ORDER_TYPE)));
	}

}
