package com.amedigital.wallet.service.strategy;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.WalletBalance;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CashBalanceStrategy implements BalanceStrategy<CashTransaction> {

    private static final Long ZERO = 0L;

    private final WalletRepository walletRepository;

    @Autowired
    public CashBalanceStrategy(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Mono<CashTransaction> check(Order order) {
        var transaction = order.getTransactions()
                .stream()
                .filter(t -> PaymentMethod.CASH == t.getPaymentMethod())
                .findFirst()
                .orElseThrow(() -> new AmeInvalidInputException("wallet_validation", "Nenhuma transação do tipo CASH encontrada."));

        if (transaction.getAmountInCents().equals(ZERO)) {
            return Mono.empty();
        }

        var walletId = order.getTransactions()
                .stream()
                .findFirst()
                .map(Transaction::getWalletId)
                .get();

        return walletRepository.findBalanceByWalletId(walletId)
                .map(WalletBalance::getCashBalance)
                .flatMap(cashBackBalance -> cashBackBalance.getAvailable() >= transaction.getAmountInCents() ?
                        Mono.empty() :
                        setDenied(transaction));
    }

    private Mono<CashTransaction> setDenied(Transaction transaction) {
        return Mono.just(transaction.copy().setStatus(TransactionStatus.DENIED).build())
                .cast(CashTransaction.class);
    }
}
