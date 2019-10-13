package com.amedigital.wallet.service.state.order.release.cashin;

import static com.amedigital.wallet.constants.enuns.ActionType.AUTHORIZE;
import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.CashStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.TransactionType.CREDIT;
import static java.util.UUID.randomUUID;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.Constants;
import com.amedigital.wallet.constants.enuns.CashStatus;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.SecondaryOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.util.FeeManager;

import reactor.core.publisher.Mono;

@Service
public class CreatedCashInReleaseState implements SecondaryOrderState {

    private final OrderRepository repository;

    @Autowired
    public CreatedCashInReleaseState(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<SecondaryOrder> create(Order order, SecondaryOrder secondaryOrder) {
        var releaseOrder = (ReleaseOrder) secondaryOrder;

        var releaseRules = releaseOrder
                .getPaymentMethodRules()
                .stream()
                .filter(t -> PaymentMethod.CREDIT_CARD == t.getPaymentMethod())
                .findFirst()
                .orElseThrow(() -> new AmeInvalidInputException("wallet_validation", "Nenhuma regra de release encontrada."));

        ZonedDateTime now = ZonedDateTime.now();

        var capturedTransaction = order.getTransactions()
                .stream()
                .filter(Transaction::isCaptured)
                .findFirst()
                .orElseThrow(() ->
                        new AmeInvalidInputException("wallet_validation", "Nenhuma transação encontrada no status CAPTURADA."));

        var listTransaction = new ArrayList<Transaction>();


        FeeManager feeManager = new FeeManager(capturedTransaction.getAmountInCents(), releaseRules.getTakeRate(), releaseRules.getTakeRateUnit());

        FeeManager.AmountParcel amountParcel = feeManager.getAmountParcel();

        CashTransaction.Builder userCashTransactionBuilder = CashTransaction.builder()
                .setCashStatus(CREATED)
                .setCashUpdatedAt(now)
                .setCashCreatedAt(now)
                .setUuid(randomUUID().toString())
                .setWalletId(capturedTransaction.getWalletId())
                .setOrderUuid(releaseOrder.getUuid())
                .setStatus(TransactionStatus.CREATED)
                .setType(CREDIT)
                .setPeerTransactionUuid(capturedTransaction.getUuid())
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setPeerWalletId(capturedTransaction.getWalletId())
                .setPeerTransactionUuid(capturedTransaction.getUuid())

                .setTakeRate(releaseRules.getTakeRate())
                .setReleaseDate(now.plus(releaseRules.getReleaseTime(), releaseRules.getReleaseTimeUnit()))

                .setReleaseTime(releaseRules.getReleaseTime())
                .setReleaseTimeUnit(releaseRules.getReleaseTimeUnit())
                .setTakeRateUnit(releaseRules.getTakeRateUnit())


                .setAmountInCents(capturedTransaction.getAmountInCents())
                .setNetAmountInCents(null)
                .setGrossAmountInCents(null)
                .setTakeRateAmountInCents(null);

        if (releaseRules.getTakeRate() != null && releaseRules.getTakeRate().doubleValue() > 0) {

            CashTransaction ameCashTransaction = CashTransaction.builder()
                    .setCashStatus(CREATED)
                    .setCashUpdatedAt(now)
                    .setCashCreatedAt(now)
                    .setUuid(randomUUID().toString())
                    .setWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
                    .setOrderUuid(releaseOrder.getUuid())
                    .setStatus(TransactionStatus.CREATED)
                    .setType(CREDIT)

                    .setLatest(true)
                    .setCreatedAt(now)
                    .setUpdatedAt(now)
                    .setPeerWalletId(capturedTransaction.getWalletId())
                    .setPeerTransactionUuid(capturedTransaction.getUuid())

                    .setTakeRateUnit(releaseRules.getTakeRateUnit())
                    .setTakeRate(releaseRules.getTakeRate())
                    .setReleaseDate(now.plus(releaseRules.getReleaseTime(), releaseRules.getReleaseTimeUnit()))
                    .setReleaseTime(releaseRules.getReleaseTime())
                    .setReleaseTimeUnit(releaseRules.getReleaseTimeUnit())

                    .setAmountInCents(amountParcel.getTakeRateAmountInCents())
                    .setTakeRateAmountInCents(amountParcel.getTakeRateAmountInCents())
                    .setGrossAmountInCents(amountParcel.getGrossAmountInCents())
                    .setNetAmountInCents(amountParcel.getAmount())



                    .build();

            userCashTransactionBuilder
                    .setAmountInCents(amountParcel.getAmount())
                    .setTakeRateAmountInCents(amountParcel.getTakeRateAmountInCents())
                    .setGrossAmountInCents(amountParcel.getGrossAmountInCents())
                    .setNetAmountInCents(amountParcel.getAmount());


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
    public Mono<SecondaryOrder> authorize(Order order, SecondaryOrder secondaryOrder) {
        var releaseOrder = (ReleaseOrder) secondaryOrder;

        ZonedDateTime now = ZonedDateTime.now();

        var transactions = releaseOrder.getTransactions()
                .stream()
                .map(t -> (CashTransaction) t)
                .map(transaction -> (Transaction) transaction.copy()
                        .setStatus(TransactionStatus.AUTHORIZED)
                        .setCashStatus(CashStatus.AUTHORIZED)
                        .setUpdatedAt(now)
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
                .cast(SecondaryOrder.class);
    }

    @Override
    public Mono<SecondaryOrder> finish(Order order, SecondaryOrder secondaryOrder) {
        return Mono.error(new AmeException(400, "release_status_error", "Não é possível LIQUIDAR uma ordem de liquidação com status CREATED "));
    }

	@Override
	public Mono<SecondaryOrder> cancel(Order order, SecondaryOrder secondaryOrder) {
		return Mono.error(new AmeException(400, "release_status_error", "Não é possível CANCELAR uma ordem de liquidação com status CREATED "));
	}

}
