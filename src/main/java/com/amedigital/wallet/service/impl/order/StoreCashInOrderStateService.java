package com.amedigital.wallet.service.impl.order;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.exceptions.AmeNotFoundException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.primary.StoreCashInOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.OrderStateService;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.order.storecashin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.amedigital.wallet.constants.enuns.OrderStatus.*;
import static com.amedigital.wallet.util.ValidatorUtil.notEmpty;
import static com.amedigital.wallet.util.ValidatorUtil.notNull;

@Service
public class StoreCashInOrderStateService implements OrderStateService<StoreCashInOrder> {

    private final WalletRepository walletRepository;

    private final Map<OrderStatus, OrderState<StoreCashInOrder>> states = new HashMap<>();

    @Autowired
    public StoreCashInOrderStateService(CreatedStoreCashInOrderState created,
                                        AuthorizedStoreCashInOrderState authorized,
                                        CapturedStoreCashInOrderState captured,
                                        CancelledStoreCashInOrderState cancelled,
                                        DeniedStoreCashInOrderState denied,
                                        WalletRepository walletRepository) {
        states.put(CREATED, created);
        states.put(AUTHORIZED, authorized);
        states.put(DENIED, denied);
        states.put(CAPTURED, captured);
        states.put(CANCELLED, cancelled);

        this.walletRepository = walletRepository;
    }

    @Override
    public Mono<StoreCashInOrder> create(StoreCashInOrder storeCashInOrder) {
        return validateFields(storeCashInOrder)
                .then(findWalletByUuid(storeCashInOrder))
                .map(Wallet::getId)
                .flatMap(Mono::justOrEmpty)
                .flatMap(walletId ->
                        states.get(storeCashInOrder.getStatus()).create(storeCashInOrder.copy()
                                .setCreditWalletId(walletId)
                                .build()))
                .switchIfEmpty(
                        Mono.error(new AmeNotFoundException("wallet_validation",
                                "Informações não encontradas da walletId " + storeCashInOrder.getCreditWalletUUID())));
    }

    @Override
    public Mono<StoreCashInOrder> authorize(StoreCashInOrder storeCashInOrder) {
        return states.get(storeCashInOrder.getStatus()).authorize(storeCashInOrder);
    }

    @Override
    public Mono<StoreCashInOrder> capture(StoreCashInOrder storeCashInOrder) {
        return states.get(storeCashInOrder.getStatus()).capture(storeCashInOrder);
    }

    @Override
    public Mono<StoreCashInOrder> cancel(StoreCashInOrder storeCashInOrder) {
        return states.get(storeCashInOrder.getStatus()).cancel(storeCashInOrder);
    }

    private Mono<Void> validateFields(StoreCashInOrder storeCashInOrder) {

        if (storeCashInOrder.getTotalAmountInCents() < 0) {
            throw new AmeInvalidInputException("wallet_validation", "O valor da ordem deve ser maior que 0.");
        }

        var transactions = storeCashInOrder.getTransactions();

        notEmpty(storeCashInOrder.getTitle(), "título da ordem");
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

            throw new AmeInvalidInputException("wallet_validation_store_cash_in",
                    "Não é possível usar cartão em uma transação de loja.");
        }

        var total = transactions
                .stream()
                .mapToLong(Transaction::getAmountInCents)
                .sum();

        if (storeCashInOrder.getTotalAmountInCents() != total) {
            throw new AmeInvalidInputException("wallet_validation",
                    "A soma dos valores das transações não é igual ao valor total da ordem.");
        }

        return Mono.empty();
    }

    private Mono<Wallet> findWalletByUuid(StoreCashInOrder storeCashInOrder) {
        return walletRepository.findByUuid(storeCashInOrder.getCreditWalletUUID());
    }
}
