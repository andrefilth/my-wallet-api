package com.amedigital.wallet.service.impl.order;

import static com.amedigital.wallet.constants.enuns.OrderStatus.AUTHORIZED;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CANCELLED;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CAPTURED;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.OrderStatus.DENIED;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.BankCashInOrder;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.OrderStateService;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.order.bankcashin.AuthorizedBankCashInOrderState;
import com.amedigital.wallet.service.state.order.bankcashin.CancelledBankCashInOrderState;
import com.amedigital.wallet.service.state.order.bankcashin.CapturedBankCashInOrderState;
import com.amedigital.wallet.service.state.order.bankcashin.CreatedBankCashInOrderState;
import com.amedigital.wallet.service.state.order.bankcashin.DeniedBankCashInOrderState;

import reactor.core.publisher.Mono;

@Service
public class BankCashInOrderStateService implements OrderStateService<BankCashInOrder> {

	private final Map<OrderStatus, OrderState<BankCashInOrder>> states = new HashMap<>();

	@Autowired
	public BankCashInOrderStateService(CreatedBankCashInOrderState created,
									AuthorizedBankCashInOrderState authorized,
									CapturedBankCashInOrderState captured,
									CancelledBankCashInOrderState cancelled,
									DeniedBankCashInOrderState denied,
									WalletRepository walletRepository) {
		states.put(CREATED, created);
		states.put(AUTHORIZED, authorized);
		states.put(CAPTURED, captured);
		states.put(CANCELLED, cancelled);
		states.put(DENIED, denied);
	}
	
	@Override
	public Mono<BankCashInOrder> create(BankCashInOrder bankCashInOrder) {
		return validateFields(bankCashInOrder)
				.then(states.get(bankCashInOrder.getStatus()).create(bankCashInOrder));
	}

	@Override
	public Mono<BankCashInOrder> authorize(BankCashInOrder bankCashInOrder) {
		return states.get(bankCashInOrder.getStatus()).authorize(bankCashInOrder);
	}

	@Override
	public Mono<BankCashInOrder> capture(BankCashInOrder bankCashInOrder) {
		return states.get(bankCashInOrder.getStatus()).capture(bankCashInOrder);
	}

	@Override
	public Mono<BankCashInOrder> cancel(BankCashInOrder bankCashInOrder) {
		return states.get(bankCashInOrder.getStatus()).cancel(bankCashInOrder);
	}

	private Mono<Void> validateFields(BankCashInOrder bankCashInOrder) {

		if (bankCashInOrder.getTotalAmountInCents() <= 0) {
			throw new AmeInvalidInputException("wallet_validation", "O valor da ordem deve ser maior que 0.");
		}

		return Mono.empty();
	}
}
