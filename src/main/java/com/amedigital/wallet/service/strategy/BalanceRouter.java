package com.amedigital.wallet.service.strategy;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Optional;

@Component
public class BalanceRouter {

    private final HashMap<PaymentMethod, BalanceStrategy> cashStrategy = new HashMap<>();

    @Autowired
    public BalanceRouter(CashBalanceStrategy cashBalanceStrategy, CashBackBalanceStrategy cashBackBalanceStrategy) {
        cashStrategy.put(PaymentMethod.CASH, cashBalanceStrategy);
        cashStrategy.put(PaymentMethod.CASH_BACK, cashBackBalanceStrategy);
    }

    public Mono<Transaction> route(PaymentMethod paymentMethod, Order order) {
        return Optional.ofNullable(cashStrategy.get(paymentMethod))
                .map(balanceStrategy -> balanceStrategy.check(order))
                .orElse(Mono.empty());
    }
}
