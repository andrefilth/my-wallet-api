package com.amedigital.wallet.service.impl.order.cashback;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.SecondaryOrderStateService;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.service.state.order.cashback.purchase.AuthorizedPurchaseCashbackState;
import com.amedigital.wallet.service.state.order.cashback.purchase.CapturedPurchaseCashbackState;
import com.amedigital.wallet.service.state.order.cashback.purchase.CreatedPurchaseCashbackState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class CashbackPurchaseOrderStateService implements SecondaryOrderStateService<PurchaseOrder, CashbackOrder> {

	private final Map<OrderStatus, SecondaryOrderState<PurchaseOrder, CashbackOrder>> states = new HashMap<>();

	@Autowired
	public CashbackPurchaseOrderStateService(WalletRepository walletRepository,
			CreatedPurchaseCashbackState createdPurchaseCashbackState,
			AuthorizedPurchaseCashbackState authorizedPurchaseCashbackState,
			CapturedPurchaseCashbackState capturedPurchaseCashbackState

	) {

		states.put(OrderStatus.CREATED, createdPurchaseCashbackState);
		states.put(OrderStatus.AUTHORIZED, authorizedPurchaseCashbackState);

		// TODO Deve ser removido
		states.put(OrderStatus.RELEASED, capturedPurchaseCashbackState);
		states.put(OrderStatus.CAPTURED, capturedPurchaseCashbackState);

	}

	@Override
	public Mono<CashbackOrder> create(PurchaseOrder purchaseOrder, CashbackOrder cashbackOrder) {
		if (purchaseOrder.getStatus().equals(OrderStatus.AUTHORIZED)) {
			return states.get(cashbackOrder.getStatus()).create(purchaseOrder, cashbackOrder);
		}
		throw new AmeException(400, "invalid_cashback",
				String.format("Não é possível criar uma ordem de cashback para um pedido de status %s",
						purchaseOrder.getStatus().toString()));
	}

	@Override
	public Mono<CashbackOrder> authorize(PurchaseOrder purchaseOrder, CashbackOrder cashbackOrder) {
		return states.get(cashbackOrder.getStatus()).authorize(purchaseOrder, cashbackOrder);
	}

	@Override
	public Mono<CashbackOrder> finish(PurchaseOrder purchaseOrder, CashbackOrder cashbackOrder) {
		if (purchaseOrder.getStatus().equals(OrderStatus.CAPTURED)) {
			return states.get(cashbackOrder.getStatus()).finish(purchaseOrder, cashbackOrder);
		}
		throw new AmeException(400, "invalid_cashback",
				String.format("Não é possível capturar uma ordem de cashback para um pedido de status %s",
						purchaseOrder.getStatus().toString()));
	}

	@Override
	public Mono<CashbackOrder> cancel(PurchaseOrder purchaseOrder, CashbackOrder cashbackOrder) {
		if (purchaseOrder.getStatus().equals(OrderStatus.AUTHORIZED)) {
			return states.get(cashbackOrder.getStatus()).cancel(purchaseOrder, cashbackOrder);
		}
		throw new AmeException(400, "invalid_cashback",
				String.format("Não é possível criar uma ordem de cashback para um pedido de status %s",
						purchaseOrder.getStatus().toString()));
	}
}
