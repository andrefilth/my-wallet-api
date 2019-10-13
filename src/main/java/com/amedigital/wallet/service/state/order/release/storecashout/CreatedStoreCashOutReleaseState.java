package com.amedigital.wallet.service.state.order.release.storecashout;

import static com.amedigital.wallet.constants.enuns.ActionType.AUTHORIZE;
import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.CashStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.TransactionType.CREDIT;
import static com.amedigital.wallet.constants.enuns.TransactionType.DEBIT;
import static java.util.UUID.randomUUID;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.Constants;
import com.amedigital.wallet.constants.enuns.CashStatus;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.util.FeeManager;

import reactor.core.publisher.Mono;

@Service
public class CreatedStoreCashOutReleaseState implements SecondaryOrderState<StoreCashOutOrder, ReleaseOrder> {

    private final OrderRepository repository;

    @Autowired
    public CreatedStoreCashOutReleaseState(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<ReleaseOrder> create(StoreCashOutOrder order, ReleaseOrder releaseOrder) {

        var releaseRule = releaseOrder
                .getPaymentMethodRules()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AmeInvalidInputException("wallet_validation", "Nenhuma regra de release encontrada."));

        var capturedTransactions = order.getTransactions()
                .stream()
                .filter(Transaction::isCaptured)
                .collect(Collectors.toList());


        if (capturedTransactions.isEmpty()) {
           return  Mono.error(new AmeInvalidInputException("wallet_validation", "Nenhuma transação encontrada no status CAPTURADA."));
        }

        var listTransaction = new ArrayList<Transaction>();
        
        var now = ZonedDateTime.now();
        var feeManager = new FeeManager(order.getTotalAmountInCents(), releaseRule.getTakeRate(), releaseRule.getTakeRateUnit());
        FeeManager.AmountParcel amountParcel = feeManager.getAmountParcel();

        if (!feeManager.isFree()) {
            
            var debitCustomerTransaction = capturedTransactions.stream()
            		.filter(transaction -> DEBIT.equals(transaction.getType()))
            		.findFirst()
            		.get();
            
            CashTransaction ameCreditFeeTransaction = CashTransaction.builder()
                    .setCashStatus(CREATED)
                    .setCashUpdatedAt(now)
                    .setCashCreatedAt(now)
                    .setUuid(randomUUID().toString())
                    .setWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
                    .setOrderUuid(releaseOrder.getUuid())
                    .setStatus(TransactionStatus.CREATED)
                    .setType(CREDIT)
                    .setTakeRate(releaseRule.getTakeRate())
                    .setTakeRateAmountInCents(amountParcel.getTakeRateAmountInCents())
                    .setTakeRateUnit(releaseRule.getTakeRateUnit())
                    .setReleaseTime(releaseRule.getReleaseTime())
                    .setReleaseTimeUnit(releaseRule.getReleaseTimeUnit())
                    .setReleaseDate(now.plus(releaseRule.getReleaseTime(), releaseRule.getReleaseTimeUnit()))
                    .setCreatedAt(now)
                    .setUpdatedAt(now)
                    .setPeerWalletId(debitCustomerTransaction.getWalletId())
                    .setPeerTransactionUuid(debitCustomerTransaction.getUuid())
                    
                    .setAmountInCents(amountParcel.getTakeRateAmountInCents()) 
                    .setTakeRateAmountInCents(amountParcel.getTakeRateAmountInCents())
                    .setGrossAmountInCents(amountParcel.getGrossAmountInCents()) 
                    .setNetAmountInCents(amountParcel.getAmount())
                    
                    .build();
            
            CashTransaction customerDebitFeeTransaction = ameCreditFeeTransaction
            		.copy()
            		.setType(DEBIT)
            		.setWalletId(debitCustomerTransaction.getWalletId())
            		.setPeerWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
            		.setPeerTransactionUuid(ameCreditFeeTransaction.getUuid())
            		.build();

            listTransaction.add(ameCreditFeeTransaction);
            listTransaction.add(customerDebitFeeTransaction);
        }

        var action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(now)
                .build();

        var releaseOrderCreated = (ReleaseOrder) releaseOrder.copy()
                .setTitle(order.getTitle())
                .setDescription(order.getDescription())
                .setTotalAmountInCents(amountParcel.getTakeRateAmountInCents())
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
    public Mono<ReleaseOrder> authorize(StoreCashOutOrder order, ReleaseOrder releaseOrder) {

        var now = ZonedDateTime.now();

        var transactions = releaseOrder.getTransactions()
                .stream()
                .map(t -> ((CashTransaction) t).copy()
                        .setStatus(TransactionStatus.AUTHORIZED)
                        .setUpdatedAt(now)
                        .setCreatedAt(now)
                        .setCashStatus(CashStatus.AUTHORIZED)
                        .setCashUpdatedAt(now)
                        .setCashCreatedAt(now)
                        .build())
                .map(Transaction.class::cast)
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
    public Mono<ReleaseOrder> finish(StoreCashOutOrder order, ReleaseOrder secondaryOrder) {
        return Mono.error(new AmeInvalidInputException("release_order_create", "Não pode finalizar uma order que não foi criada!"));
    }

	@Override
	public Mono<ReleaseOrder> cancel(StoreCashOutOrder order, ReleaseOrder secondaryOrder) {
		return Mono.error(new AmeInvalidInputException("release_order_create", "Não pode cancelar uma order que não foi criada!"));
	}
}
