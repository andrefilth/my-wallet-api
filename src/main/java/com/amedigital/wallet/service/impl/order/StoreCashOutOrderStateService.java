package com.amedigital.wallet.service.impl.order;

import static com.amedigital.wallet.constants.enuns.OrderStatus.AUTHORIZED;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CANCELLED;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CAPTURED;
import static com.amedigital.wallet.constants.enuns.OrderStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.OrderStatus.DENIED;
import static com.amedigital.wallet.util.ValidatorUtil.notEmpty;
import static com.amedigital.wallet.util.ValidatorUtil.notNull;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.exceptions.AmeNotFoundException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.OrderStateService;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.order.storecashout.AuthorizedStoreCashOutOrderState;
import com.amedigital.wallet.service.state.order.storecashout.CancelledStoreCashOutOrderState;
import com.amedigital.wallet.service.state.order.storecashout.CapturedStoreCashOutOrderState;
import com.amedigital.wallet.service.state.order.storecashout.CreatedStoreCashOutOrderState;
import com.amedigital.wallet.service.state.order.storecashout.DeniedStoreCashOutOrderState;

import reactor.core.publisher.Mono;

@Service
public class StoreCashOutOrderStateService implements OrderStateService<StoreCashOutOrder> {

    private final WalletRepository walletRepository;

    private final Map<OrderStatus, OrderState<StoreCashOutOrder>> states = new HashMap<>();

    @Autowired
    public StoreCashOutOrderStateService(CreatedStoreCashOutOrderState created,
                                        AuthorizedStoreCashOutOrderState authorized,
                                        CapturedStoreCashOutOrderState captured,
                                        CancelledStoreCashOutOrderState cancelled,
                                        DeniedStoreCashOutOrderState denied,
                                        WalletRepository walletRepository) {
        states.put(CREATED, created);
        states.put(AUTHORIZED, authorized);
        states.put(DENIED, denied);
        states.put(CAPTURED, captured);
        states.put(CANCELLED, cancelled);

        this.walletRepository = walletRepository;
    }

    @Override
    public Mono<StoreCashOutOrder> create(StoreCashOutOrder storeCashOutOrder) {
        return validateFields(storeCashOutOrder)
				.then(checkAvailableBalance(storeCashOutOrder))
				.then(findWalletByUuid(storeCashOutOrder))
				.map(Wallet::getId)
				.flatMap(Mono::justOrEmpty)
				.flatMap(creditWalletId ->
					states.get(storeCashOutOrder.getStatus()).create(storeCashOutOrder.copy()
							.setCreditWalletId(creditWalletId)
							.setCreatedByWalletId(creditWalletId)
							.build()))
				.switchIfEmpty(
						Mono.error(new AmeNotFoundException("wallet_validation",
								"Informacoes nao encontradas da walletId " + storeCashOutOrder.getCreditWalletUUID())));
    }

    @Override
    public Mono<StoreCashOutOrder> authorize(StoreCashOutOrder storeCashOutOrder) {
        return states.get(storeCashOutOrder.getStatus()).authorize(storeCashOutOrder);
    }

    @Override
    public Mono<StoreCashOutOrder> capture(StoreCashOutOrder storeCashOutOrder) {
        return states.get(storeCashOutOrder.getStatus()).capture(storeCashOutOrder);
    }

    @Override
    public Mono<StoreCashOutOrder> cancel(StoreCashOutOrder storeCashOutOrder) {
        return states.get(storeCashOutOrder.getStatus()).cancel(storeCashOutOrder);
    }

    private Mono<Void> validateFields(StoreCashOutOrder storeCashOutOrder) {

        if (storeCashOutOrder.getTotalAmountInCents() < 0) {
            throw new AmeInvalidInputException("wallet_validation", "O valor da ordem deve ser maior que 0.");
        }

        var transactions = storeCashOutOrder.getTransactions();

        notEmpty(storeCashOutOrder.getTitle(), "título da ordem");
        notNull(transactions, "métodos de pagamento");

        if (transactions.stream().anyMatch(t -> t.getAmountInCents() <= 0)) {
            throw new AmeInvalidInputException("wallet_validation",
                    "Uma paymentMethod não pode ter o valor menor ou igual a 0.");
        }

        if (transactions.stream()
                .filter(transaction -> PaymentMethod.CREDIT_CARD.equals(transaction.getPaymentMethod()))
                .findAny()
                .map($ -> Boolean.TRUE)
                .orElse(Boolean.FALSE)) {

            throw new AmeInvalidInputException("wallet_validation_store_cash_out",
                    "Não é possível usar cartão em uma transação de loja.");
        }

        var total = transactions
                .stream()
                .mapToLong(Transaction::getAmountInCents)
                .sum();

        if (storeCashOutOrder.getTotalAmountInCents() != total) {
            throw new AmeInvalidInputException("wallet_validation",
                    "A soma dos valores das transações não é igual ao valor total da ordem.");
        }

        return Mono.empty();
    }
    
	private Mono<Void> checkAvailableBalance(StoreCashOutOrder order) {

		return walletRepository.findBalanceByWalletId(order.getDebitWalletId()).flatMap(b -> {
			if (b.getCashBalance().getAvailable() >= order.getTotalAmountInCents()) {
				return Mono.empty();
			} else {
				return Mono.error(new AmeInvalidInputException("wallet_insuficient_balance", "Saldo insuficiente para realizar a transferencia STORE_CASH_OUT."));
			}
		});
	}

	private Mono<Wallet> findWalletByUuid(StoreCashOutOrder storeCashOutOrder) {
		return walletRepository.findByUuid(storeCashOutOrder.getCreditWalletUUID());
	}
}
