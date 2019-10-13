package com.amedigital.wallet.service.impl.order;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.exceptions.AmeNotFoundException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.OrderStateService;
import com.amedigital.wallet.service.state.OrderState;
import com.amedigital.wallet.service.state.order.transferbetweenwallets.*;
import com.amedigital.wallet.service.strategy.BalanceRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amedigital.wallet.constants.enuns.OrderStatus.*;
import static com.amedigital.wallet.util.ValidatorUtil.notEmpty;
import static com.amedigital.wallet.util.ValidatorUtil.notNull;

@Service
public class TransferBetweenWalletsOrderStateService implements OrderStateService<TransferBetweenWalletsOrder> {

    private final WalletRepository walletRepository;
    private final BalanceRouter router;

    private final Map<OrderStatus, OrderState> states = new HashMap<>();

    @Autowired
    public TransferBetweenWalletsOrderStateService(CreatedTransferBetweenWalletsOrderState created,
                                                   AuthorizedTransferBetweenWalletsOrderState authorized,
                                                   CapturedTransferBetweenWalletsOrderState captured,
                                                   CancelledTransferBetweenWalletsOrderState cancelled,
                                                   DeniedTransferBetweenWalletsOrderState denied,
                                                   WalletRepository walletRepository,
                                                   BalanceRouter router) {
        states.put(CREATED, created);
        states.put(AUTHORIZED, authorized);
        states.put(DENIED, denied);
        states.put(CAPTURED, captured);
        states.put(CANCELLED, cancelled);

        this.walletRepository = walletRepository;
        this.router = router;
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> create(TransferBetweenWalletsOrder transferBetweenWalletsOrder) {
        return findWalletByUuidOrOwner(transferBetweenWalletsOrder)
                .filter(Wallet::isCustomer)
                .flatMap(wallet -> validateFields(wallet, transferBetweenWalletsOrder)
                        .flatMap($ -> processWithBalance(transferBetweenWalletsOrder).then())
                        .then(
                                states.get(transferBetweenWalletsOrder.getStatus()).create(transferBetweenWalletsOrder.copy()
                                .setToWalletId(wallet.getId().get())
                                .build())))
                .switchIfEmpty(
                        Mono.error(new AmeNotFoundException("wallet_validation",
                                "Informações não encontradas da walletId " + transferBetweenWalletsOrder.getToWalletUuid())))
                .cast(TransferBetweenWalletsOrder.class);
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> authorize(TransferBetweenWalletsOrder transferBetweenWalletsOrder) {
        return states.get(transferBetweenWalletsOrder.getStatus()).authorize(transferBetweenWalletsOrder);
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> capture(TransferBetweenWalletsOrder transferBetweenWalletsOrder) {
        return states.get(transferBetweenWalletsOrder.getStatus()).capture(transferBetweenWalletsOrder);
    }

    @Override
    public Mono<TransferBetweenWalletsOrder> cancel(TransferBetweenWalletsOrder transferBetweenWalletsOrder) {
        return states.get(transferBetweenWalletsOrder.getStatus()).cancel(transferBetweenWalletsOrder);
    }


    private Flux<Transaction> processWithBalance(TransferBetweenWalletsOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(t -> router.route(t.getPaymentMethod(), order))
                .flatMap($ -> Flux.fromStream(order.getTransactions()
                        .stream())
                        .map(t2 -> t2.copy().setStatus(TransactionStatus.DENIED).build())
                        .map(Transaction.class::cast));
    }


    private Mono<Void> validateFields(Wallet toWallet, TransferBetweenWalletsOrder transferBetweenWalletsOrder) {
        if (toWallet.getId().get().equals(transferBetweenWalletsOrder.getCreatedByWalletId())) {
            throw new AmeInvalidInputException("wallet_validation", "Não é possível realizar uma transferencia para a mesma carteira.");
        }

        if (transferBetweenWalletsOrder.getTotalAmountInCents() <= 0) {
            throw new AmeInvalidInputException("wallet_validation", "O valor da ordem deve ser maior que 0.");
        }

        List<Transaction> transactions = transferBetweenWalletsOrder.getTransactions();

        notEmpty(transferBetweenWalletsOrder.getTitle(), "título da ordem");
        notNull(transactions, "métodos de pagamento");

        if (transactions.stream().anyMatch(t -> t.getAmountInCents() <= 0)) {
            throw new AmeInvalidInputException("wallet_validation",
                    "Uma paymentMethod não pode ter o valor menor ou igual a 0.");
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

        if (transferBetweenWalletsOrder.getTotalAmountInCents() != total) {
            throw new AmeInvalidInputException("wallet_validation",
                    "A soma dos valores das transações não é igual ao valor total da ordem.");
        }

        return Mono.empty();
    }

    private Mono<Wallet> findWalletByUuidOrOwner(TransferBetweenWalletsOrder transferBetweenWalletsOrder) {

        if(transferBetweenWalletsOrder.getToWalletUuid() != null) {
            return walletRepository.findByUuid(transferBetweenWalletsOrder.getToWalletUuid());
        } else {
            return walletRepository.findByOwnerUuid(transferBetweenWalletsOrder.getToOwnerUuid());
        }


    }
}
