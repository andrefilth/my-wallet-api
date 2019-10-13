package com.amedigital.wallet.service.impl.order;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.CashInOrder;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.service.OrderStateService;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.order.cashin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amedigital.wallet.constants.enuns.OrderStatus.*;
import static com.amedigital.wallet.util.ValidatorUtil.notEmpty;
import static com.amedigital.wallet.util.ValidatorUtil.notNull;

@Service
public class CashInOrderStateService implements OrderStateService<CashInOrder> {

    private final Map<OrderStatus, OrderState> states = new HashMap<>();

    @Autowired
    public CashInOrderStateService(CreatedCashInOrderState createdCashInOrderState,
                                   AuthorizedCashInOrderState authorizedCashInOrderState,
                                   DeniedCashInOrderState deniedCashInOrderState,
                                   CapturedCashInOrderState capturedCashInOrderState,
                                   CancelledCashInOrderState cancelledCashInOrderState) {

        states.put(CREATED, createdCashInOrderState);
        states.put(AUTHORIZED, authorizedCashInOrderState);
        states.put(DENIED, deniedCashInOrderState);
        states.put(CAPTURED, capturedCashInOrderState);
        states.put(CANCELLED, cancelledCashInOrderState);

    }


    @Override
    public Mono<CashInOrder> create(CashInOrder cashInOrder) {
        return validateFields(cashInOrder).then(states.get(cashInOrder.getStatus()).create(cashInOrder));
    }

    @Override
    public Mono<CashInOrder> authorize(CashInOrder cashInOrder) {
        return states.get(cashInOrder.getStatus()).authorize(cashInOrder);
    }

    @Override
    public Mono<CashInOrder> capture(CashInOrder cashInOrder) {
        return states.get(cashInOrder.getStatus()).capture(cashInOrder);
    }

    @Override
    public Mono<CashInOrder> cancel(CashInOrder cashInOrder) {
        return states.get(cashInOrder.getStatus()).cancel(cashInOrder);

    }

    private Mono<Void> validateFields(CashInOrder cashInOrder) {
        if (cashInOrder.getTotalAmountInCents() <= 0) {
            throw new AmeInvalidInputException("wallet_validation", "O valor da ordem deve ser maior que 0.");
        }

        List<Transaction> transactions = cashInOrder.getTransactions();

        notEmpty(cashInOrder.getTitle(), "título da ordem");
        notNull(transactions, "métodos de pagamento");

        if (transactions.stream().anyMatch(t -> t.getAmountInCents() <= 0)) {
            throw new AmeInvalidInputException("wallet_validation",
                    "Uma paymentMethod não pode ter o valor menor ou igual a 0.");
        }

        if (transactions.stream().anyMatch(t -> !PaymentMethod.CREDIT_CARD.equals(t.getPaymentMethod()))) {
            throw new AmeInvalidInputException("wallet_validation_cash_in",
                    "Não é possível efetuar um CashIn com métodos de pagamento diferentes de CREDIT_CARD");
        }

        if (transactions.stream()
                .filter(transaction -> PaymentMethod.CREDIT_CARD.equals(transaction.getPaymentMethod()))
                .anyMatch(transaction -> ((CreditCardTransaction) transaction).getNumberOfInstallments() != 1)) {

            throw new AmeInvalidInputException("wallet_validation_cash_in",
                    "Não é possível parcelar um cash in.");
        }

        var total = transactions
                .stream()
                .mapToLong(Transaction::getAmountInCents)
                .sum();

        if (cashInOrder.getTotalAmountInCents() != total) {
            throw new AmeInvalidInputException("wallet_validation",
                    "A soma dos valores das transações não é igual ao valor total da ordem.");
        }

        return Mono.empty();
    }


}
