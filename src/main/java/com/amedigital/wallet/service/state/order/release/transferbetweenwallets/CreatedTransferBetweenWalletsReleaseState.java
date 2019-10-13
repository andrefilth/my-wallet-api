package com.amedigital.wallet.service.state.order.release.transferbetweenwallets;

import com.amedigital.wallet.constants.Constants;
import com.amedigital.wallet.constants.enuns.CashStatus;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.amedigital.wallet.constants.Constants.RELEASE_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.authorizedMessageException;
import static com.amedigital.wallet.constants.enuns.ActionType.AUTHORIZE;
import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.CashStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.TransactionType.CREDIT;
import static java.util.UUID.randomUUID;

@Service
public class CreatedTransferBetweenWalletsReleaseState implements SecondaryOrderState<TransferBetweenWalletsOrder, ReleaseOrder> {

    private final OrderRepository repository;

    @Autowired
    public CreatedTransferBetweenWalletsReleaseState(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<ReleaseOrder> create(TransferBetweenWalletsOrder order, ReleaseOrder releaseOrder) {

        ZonedDateTime now = ZonedDateTime.now();

        var capturedTransactions = order.getTransactions()
                .stream()
                .filter(Transaction::isCaptured)
                .collect(Collectors.toList());

        var releaseRule = releaseOrder.getPaymentMethodRules().stream().findFirst().orElseThrow(() -> new AmeInvalidInputException("wallet_validation", "Nenhuma regra de release encontrada"));

        if(capturedTransactions.isEmpty()){
            throw new AmeInvalidInputException("wallet_validation", "Nenhuma transação encontrada no status CAPTURADA.");
        }

        var listTransaction = new ArrayList<Transaction>();

        Transaction anyTransaction = capturedTransactions.get(0);
        var orderTotalAmountInCents = capturedTransactions.stream().map(Transaction::getAmountInCents).reduce(0L, (c, acc)  ->  acc + c);

        CashTransaction.Builder userCashTransactionBuilder = CashTransaction.builder()
                .setCashStatus(CREATED)
                .setCashUpdatedAt(now)
                .setCashCreatedAt(now)
                .setUuid(randomUUID().toString())
                .setWalletId(anyTransaction.getPeerWalletId())
                .setOrderUuid(releaseOrder.getUuid())
                .setStatus(TransactionStatus.CREATED)
                .setType(CREDIT)
                .setTakeRate(releaseRule.getTakeRate())
                .setReleaseDate(now.plus(releaseRule.getReleaseTime(), releaseRule.getReleaseTimeUnit()))
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setPeerWalletId(anyTransaction.getWalletId())
                .setPeerTransactionUuid(anyTransaction.getUuid())
                .setAmountInCents(orderTotalAmountInCents);

        if (releaseRule.getTakeRate() > 0) {
            var value = new BigDecimal(String.valueOf(releaseRule.getTakeRate() / 100));
            var takeRateCalc = value.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP); // 10 | 0.10
            var userReceive = BigDecimal.ONE.subtract(takeRateCalc); //  1 - 0.10 | 0.90

            CashTransaction ameCashTransaction = CashTransaction.builder()
                    .setCashStatus(CREATED)
                    .setCashUpdatedAt(now)
                    .setCashCreatedAt(now)
                    .setUuid(randomUUID().toString())
                    .setWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
                    .setOrderUuid(releaseOrder.getUuid())
                    .setStatus(TransactionStatus.CREATED)
                    .setType(CREDIT)
                    .setTakeRate(releaseRule.getTakeRate())
                    .setReleaseDate(now.plus(releaseRule.getReleaseTime(), releaseRule.getReleaseTimeUnit()))
                    .setLatest(true)
                    .setCreatedAt(now)
                    .setUpdatedAt(now)
                    .setPeerWalletId(anyTransaction.getPeerWalletId())
                    .setPeerTransactionUuid(anyTransaction.getUuid())
                    .setAmountInCents(BigDecimal.valueOf(orderTotalAmountInCents).multiply(takeRateCalc).longValue())
                    .build();

            userCashTransactionBuilder
                    .setAmountInCents(BigDecimal.valueOf(orderTotalAmountInCents).multiply(userReceive).longValue());

            listTransaction.add(ameCashTransaction);
        }

        listTransaction.add(userCashTransactionBuilder.build());

        var action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(now)
                .build();

        var releaseOrderCreated = (ReleaseOrder) releaseOrder.copy()
                .setTitle(order.getTitle())
                .setDescription(order.getDescription())
                .setTotalAmountInCents(order.getTotalAmountInCents())
                .setAuthorizationMethod(order.getAuthorizationMethod())
                .setTransactions(listTransaction)
                .setAction(action)
                .setReferenceOrderUuid(order.getUuid())
                .setOrderDetailUuid(order.getOrderDetailUuid())
                .build();


        return repository
                .save(releaseOrderCreated)
                .cast(ReleaseOrder.class)
                .flatMap(rc -> authorize(order, rc));
    }

    @Override
    public Mono<ReleaseOrder> authorize(TransferBetweenWalletsOrder order, ReleaseOrder releaseOrder) {

        ZonedDateTime now = ZonedDateTime.now();

        var transactions = releaseOrder.getTransactions()
                .stream()
                .map(t -> (Transaction) ((CashTransaction) t).copy()
                        .setStatus(TransactionStatus.AUTHORIZED)
                        .setUpdatedAt(now)
                        .setCreatedAt(now)
                        .setCashStatus(CashStatus.AUTHORIZED)
                        .setCashUpdatedAt(now)
                        .setCashCreatedAt(now)
                        .build())
                .collect(Collectors.toList());

        var action = new Action.Builder()
                .setType(AUTHORIZE)
                .setParentId(releaseOrder.getAction().getId())
                .setCreatedAt(now)
                .build();

        var releaseOrderAuthorized = releaseOrder.copy()
                .setStatus(OrderStatus.AUTHORIZED)
                .setUpdatedAt(now)
                .setAction(action)
                .setTransactions(transactions)
                .build();

        return repository
                .save(releaseOrderAuthorized)
                .cast(ReleaseOrder.class);
    }

    @Override
    public Mono<ReleaseOrder> finish(TransferBetweenWalletsOrder order, ReleaseOrder secondaryOrder) {
        return null;
    }

	@Override
	public Mono<ReleaseOrder> cancel(TransferBetweenWalletsOrder order, ReleaseOrder secondaryOrder) {
		 throw new AmeInvalidInputException("wallet_validation",
	                authorizedMessageException("cancel", RELEASE_ORDER_TYPE));
	}
}
