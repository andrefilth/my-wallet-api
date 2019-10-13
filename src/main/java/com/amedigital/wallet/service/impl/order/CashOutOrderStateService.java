package com.amedigital.wallet.service.impl.order;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.CashOutOrder;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.OrderStateService;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.order.cashout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.amedigital.wallet.constants.enuns.OrderStatus.*;

@Service
public class CashOutOrderStateService implements OrderStateService<CashOutOrder>{

	private final Map<OrderStatus, OrderState<CashOutOrder>> states = new HashMap<>();

	private final WalletRepository walletRepository;
	
    @Autowired
    public CashOutOrderStateService(CreatedCashOutOrderState created,
									AuthorizedCashOutOrderState authorized,
									CapturedCashOutOrderState captured,
									CancelledCashOutOrderState cancelled,
									DeniedCashOutOrderState denied,
									WalletRepository walletRepository) {
        states.put(CREATED, created);
        states.put(AUTHORIZED, authorized);
        states.put(CAPTURED, captured);
        states.put(CANCELLED, cancelled);
        states.put(DENIED, denied);
		this.walletRepository = walletRepository;
    }
	
	@Override
	public Mono<CashOutOrder> create(CashOutOrder cashOutOrder) {
		return validateFields(cashOutOrder)
				.then(checkAvailableBalance(cashOutOrder))
				.then(states.get(cashOutOrder.getStatus()).create(cashOutOrder));
	}

	@Override
	public Mono<CashOutOrder> authorize(CashOutOrder cashOutOrder) {
		return states.get(cashOutOrder.getStatus()).authorize(cashOutOrder);
	}

	@Override
	public Mono<CashOutOrder> capture(CashOutOrder cashOutOrder) {
		return states.get(cashOutOrder.getStatus()).capture(cashOutOrder);
	}

	@Override
	public Mono<CashOutOrder> cancel(CashOutOrder cashOutOrder) {
		return states.get(cashOutOrder.getStatus()).cancel(cashOutOrder);
	}

	private Mono<Void> validateFields(CashOutOrder cashOutOrder) {

		if (cashOutOrder.getTotalAmountInCents() <= 0) {
			throw new AmeInvalidInputException("wallet_validation", "O valor da ordem deve ser maior que 0.");
		}

		return Mono.empty();
	}

	private Mono<Void> checkAvailableBalance(CashOutOrder order) {

		return walletRepository.findBalanceByWalletId(order.getCreatedByWalletId()).flatMap(b -> {
			if (b.getCashBalance().getAvailable() >= order.getTotalAmountInCents()) {
				return Mono.empty();
			} else {
				return Mono.error(new AmeInvalidInputException("wallet_insuficient_balance", "Saldo insuficiente para realizar a transferencia CASH_OUT."));
			}
		});

	}
}
