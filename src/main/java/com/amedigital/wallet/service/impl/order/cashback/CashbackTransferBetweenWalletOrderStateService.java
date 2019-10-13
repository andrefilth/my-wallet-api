package com.amedigital.wallet.service.impl.order.cashback;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.cashback.transferbetweenwallets.AuthorizedTransferBetweenWalletsCashbackState;
import com.amedigital.wallet.service.state.order.cashback.transferbetweenwallets.CapturedTransferBetweenWalletsCashbackState;
import com.amedigital.wallet.service.state.order.cashback.transferbetweenwallets.CreatedTransferBetweenWalletsCashbackState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class CashbackTransferBetweenWalletOrderStateService implements SecondaryOrderStateService<TransferBetweenWalletsOrder, CashbackOrder> {


    private final Map<OrderStatus, SecondaryOrderState<TransferBetweenWalletsOrder, CashbackOrder>> states = new HashMap<>();
    private final WalletRepository walletRepository;

    @Autowired
    public CashbackTransferBetweenWalletOrderStateService(WalletRepository walletRepository,
                                                          CreatedTransferBetweenWalletsCashbackState createdTransferBetweenWalletsCashbackState,
                                                          AuthorizedTransferBetweenWalletsCashbackState transferBetweenWalletsCashbackState,
                                                          CapturedTransferBetweenWalletsCashbackState capturedTransferBetweenWalletsCashbackState) {
        this.walletRepository = walletRepository;


        states.put(OrderStatus.CREATED, createdTransferBetweenWalletsCashbackState);
        states.put(OrderStatus.AUTHORIZED, transferBetweenWalletsCashbackState);
        states.put(OrderStatus.RELEASED,  capturedTransferBetweenWalletsCashbackState);

    }

    @Override
    public Mono<CashbackOrder> create(TransferBetweenWalletsOrder transferBetweenWalletsOrder, CashbackOrder cashbackOrder) {
        return null;
    }

    @Override
    public Mono<CashbackOrder> authorize(TransferBetweenWalletsOrder transferBetweenWalletsOrder, CashbackOrder cashbackOrder) {
        return null;
    }

    @Override
    public Mono<CashbackOrder> finish(TransferBetweenWalletsOrder transferBetweenWalletsOrder, CashbackOrder cashbackOrder) {
        return null;
    }

	@Override
	public Mono<CashbackOrder> cancel(TransferBetweenWalletsOrder t, CashbackOrder k) {
		// TODO Auto-generated method stub
		return null;
	}
}