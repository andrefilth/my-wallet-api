package com.amedigital.wallet.service.state.order.release.purchase;

import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.CashStatus.CREATED;
import static com.amedigital.wallet.constants.enuns.TransactionType.CREDIT;
import static java.util.UUID.randomUUID;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.Constants;
import com.amedigital.wallet.constants.enuns.ActionType;
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
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.util.FeeManager;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreatedPurchaseReleaseState implements SecondaryOrderState {

    private OrderRepository repository;

    @Autowired
    public CreatedPurchaseReleaseState(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<SecondaryOrder> create(Order order, SecondaryOrder secondaryOrder) {
        var releaseOrder = (ReleaseOrder) secondaryOrder;

        var now = ZonedDateTime.now();
        Action action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(now)
                .build();

        return Mono.just(order)
                .flatMapMany(o -> Flux.fromStream(o.getTransactions().stream()))
                .flatMap(t -> {

                    var anyCapturedTransaction = order.getTransactions()
                            .stream()
                            .filter(Transaction::isCaptured)
                            .findFirst()
                            .orElseThrow(() ->
                                    new AmeInvalidInputException("wallet_validation", "Nenhuma transação encontrada no status CAPTURADA."));

                    var releaseTransactionTemplate = CashTransaction.builder()
                            .setCashStatus(CREATED)
                            .setCashUpdatedAt(now)
                            .setCashCreatedAt(now)
                            .setOrderUuid(releaseOrder.getUuid())
                            .setStatus(TransactionStatus.CREATED)
                            .setType(CREDIT)
                            .setLatest(true)
                            .setCreatedAt(now)
                            .setUpdatedAt(now)
                            .build();

                    if (PaymentMethod.CREDIT_CARD == t.getPaymentMethod()) {
                        CreditCardTransaction cct = (CreditCardTransaction) t;

                        var  releaseRule = releaseOrder
                                .getPaymentMethodRules()
                                .stream()
                                .filter(tr -> PaymentMethod.CREDIT_CARD == tr.getPaymentMethod())
                                .findFirst()
                                .orElseThrow(() -> new AmeInvalidInputException("wallet_validation", "Não há regra de RELEASE correspondente ao método de pagamento."));

                        var feeManager = new FeeManager(t.getAmountInCents(), releaseRule.getTakeRate(), releaseRule.getTakeRateUnit());
                        var installments = cct.getNumberOfInstallments();


                        var merchantTransactions = feeManager
                                .calcInstallmentsAmount(installments)
                                .stream()
                                .map(amountParcel -> {
                                    return releaseTransactionTemplate
                                            .copy()
                                            .setUuid(randomUUID().toString())
                                            .setWalletId(anyCapturedTransaction.getPeerWalletId())
                                            .setPeerWalletId(anyCapturedTransaction.getWalletId())
                                            .setPeerTransactionUuid(anyCapturedTransaction.getUuid())


                                            .setAmountInCents(amountParcel.getAmount()) // TODO: Popular o valor liquido da transacao
                                            .setTakeRateAmountInCents(amountParcel.getTakeRateAmountInCents()) // TODO: Popular o valor da taxa em centavos
                                            .setGrossAmountInCents(amountParcel.getGrossAmountInCents()) // TODO: Popular o valor da taxa bruto que é o mesmo que da transacao original
                                            .setNetAmountInCents(amountParcel.getAmount()) // TODO:  Qual o valor liquido

                                            .setTakeRate(releaseRule.getTakeRate())
                                            .setTakeRateUnit(releaseRule.getTakeRateUnit())
                                            .setReleaseDate(now.plus(releaseRule.getReleaseTime() * amountParcel.getParcelNumber(), releaseRule.getReleaseTimeUnit()))
                                            .setReleaseTime(releaseRule.getReleaseTime())
                                            .setReleaseTimeUnit(releaseRule.getReleaseTimeUnit())

                                            .build();
                                })
                                .collect(Collectors.toList());

                        var merchantFlux = Flux.fromStream(merchantTransactions.stream());

                        if (!feeManager.isFree()) {

                            var managerTransactions = feeManager
                                    .calcInstallmentsAmount(installments)
                                    .stream()
                                    .map(amountParcel -> {

                                        return releaseTransactionTemplate
                                                .copy()
                                                .setUuid(randomUUID().toString())
                                                .setWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
                                                .setPeerWalletId(anyCapturedTransaction.getWalletId())
                                                .setPeerTransactionUuid(anyCapturedTransaction.getUuid())

                                                .setTakeRateUnit(releaseRule.getTakeRateUnit())
                                                .setTakeRate(releaseRule.getTakeRate())

                                                .setAmountInCents(amountParcel.getTakeRateAmountInCents()) // TODO: Popular o valor utilizado para calculo saldo
                                                .setTakeRateAmountInCents(amountParcel.getTakeRateAmountInCents()) // TODO: Popular o valor da taxa em centavos
                                                .setGrossAmountInCents(amountParcel.getGrossAmountInCents()) // TODO: Popular o valor da taxa bruto que é o mesmo que da transacao original
                                                .setNetAmountInCents(amountParcel.getAmount()) // TODO:  Qual o valor liquido

                                                .setReleaseDate(now.plus(releaseRule.getReleaseTime() * amountParcel.getParcelNumber(), releaseRule.getReleaseTimeUnit()))
                                                .setReleaseTime(releaseRule.getReleaseTime())
                                                .setReleaseTimeUnit(releaseRule.getReleaseTimeUnit())

                                                .build();
                                    })
                                    .collect(Collectors.toList());

                            return merchantFlux.mergeWith(Flux.fromStream(managerTransactions.stream()));
                        }

                        return merchantFlux;

                    } else if(PaymentMethod.CASH == t.getPaymentMethod()) {

                        var releaseRule = releaseOrder
                                .getPaymentMethodRules()
                                .stream()
                                .filter(tr -> PaymentMethod.CASH == tr.getPaymentMethod())
                                .findFirst()
                                .orElseThrow(() ->
                                        new AmeInvalidInputException("wallet_validation", "Não há regra de RELEASE correspondente ao método de pagamento."));

                        var feeManager = new FeeManager(t.getAmountInCents(), releaseRule.getTakeRate(), releaseRule.getTakeRateUnit());

                        FeeManager.AmountParcel amountParcel = feeManager.getAmountParcel();
                        var merchantTr = releaseTransactionTemplate
                                .copy()

                                .setUuid(randomUUID().toString())
                                .setWalletId(anyCapturedTransaction.getPeerWalletId())
                                .setPeerWalletId(anyCapturedTransaction.getWalletId())
                                .setPeerTransactionUuid(anyCapturedTransaction.getUuid())

                                .setTakeRate(releaseRule.getTakeRate())
                                .setTakeRateUnit(releaseRule.getTakeRateUnit())

                                .setAmountInCents(amountParcel.getAmount()) // TODO: Popular o valor utilizado para calculo saldo
                                .setTakeRateAmountInCents(amountParcel.getTakeRateAmountInCents()) // TODO: Popular o valor da taxa em centavos
                                .setGrossAmountInCents(amountParcel.getGrossAmountInCents()) // TODO: Popular o valor da taxa bruto que é o mesmo que da transacao original
                                .setNetAmountInCents(amountParcel.getAmount()) // TODO:  Qual o valor liquido

                                .setReleaseDate(now.plus(releaseRule.getReleaseTime(), releaseRule.getReleaseTimeUnit()))

                                .setReleaseTime(releaseRule.getReleaseTime())
                                .setReleaseTimeUnit(releaseRule.getReleaseTimeUnit())

                                .build();

                        if(!feeManager.isFree()) {

                            var managerTr = releaseTransactionTemplate
                                    .copy()
                                    .setUuid(randomUUID().toString())
                                    .setWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
                                    .setPeerWalletId(anyCapturedTransaction.getWalletId())
                                    .setPeerTransactionUuid(anyCapturedTransaction.getUuid())

                                    .setTakeRate(releaseRule.getTakeRate())
                                    .setReleaseDate(now.plus(releaseRule.getReleaseTime(), releaseRule.getReleaseTimeUnit()))

                                    .setAmountInCents(amountParcel.getTakeRateAmountInCents()) // TODO: Popular o valor utilizado para calculo saldo
                                    .setTakeRateAmountInCents(amountParcel.getTakeRateAmountInCents()) // TODO: Popular o valor da taxa em centavos
                                    .setGrossAmountInCents(amountParcel.getGrossAmountInCents()) // TODO: Popular o valor da taxa bruto que é o mesmo que da transacao original
                                    .setNetAmountInCents(amountParcel.getAmount()) // TODO:  Qual o valor liquido

                                    .setTakeRate(releaseRule.getTakeRate())
                                    .setReleaseTime(releaseRule.getReleaseTime())
                                    .setReleaseTimeUnit(releaseRule.getReleaseTimeUnit())

                                    .setTakeRateUnit(releaseRule.getTakeRateUnit())

                                    .build();

                            return Flux.just(merchantTr, managerTr);

                        }

                        return Flux.just(merchantTr);

                    } else {
                        return Flux.error(new AmeInvalidInputException("wallet_validation", "O método de pagamento não é suportado"));

                    }
                })
                .cast(Transaction.class)
                .collectList()
                .map(transactionList -> (ReleaseOrder) releaseOrder.copy()
                        .setStatus(OrderStatus.CREATED)
                        .setTransactions(transactionList)
                        .setAction(action)
                        .setReferenceOrderUuid(order.getUuid())
                        .setTotalAmountInCents(order.getTotalAmountInCents())
                        .setTitle(order.getTitle())
                        .setAuthorizationMethod(order.getAuthorizationMethod())
                        .setDescription(order.getDescription())
                        .setCreatedAt(now)
                        .setUpdatedAt(now)
                        .setCreatedByWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
                        .build())
                .flatMap(repository::save)
                .cast(ReleaseOrder.class)
                .flatMap(ro -> authorize(order, ro));
    }




    @Override
    public Mono<SecondaryOrder> authorize(Order order, SecondaryOrder secondaryOrder) {
        var releaseOrder = (ReleaseOrder) secondaryOrder;

        var now = ZonedDateTime.now();

        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.AUTHORIZE)
                .setParentId(releaseOrder.getAction().getId())
                .build();

        return Flux.fromStream(releaseOrder.getTransactions().stream())
                .map(t -> (Transaction) ((CashTransaction) t).copy()
                        .setStatus(TransactionStatus.AUTHORIZED)
                        .setCreatedAt(now)
                        .setUpdatedAt(now)
                        .setCashStatus(CashStatus.AUTHORIZED)
                        .setCashUpdatedAt(now)
                        .setCashCreatedAt(now)
                        .build())
                .collectList()
                .map(trs -> {
                    var builder = releaseOrder.copy()
                            .setTransactions(trs)
                            .setAction(action);

                    if (trs.stream().allMatch(t -> TransactionStatus.AUTHORIZED.equals(t.getStatus()))) {
                        builder.setStatus(OrderStatus.AUTHORIZED);
                    }
                    return builder.build();
                }).flatMap(repository::save)
                .cast(SecondaryOrder.class);
    }

    @Override
    public Mono<SecondaryOrder> finish(Order order, SecondaryOrder secondaryOrder) {
        throw new AmeException(400, "release_status_error", "Não é possível LIQUIDAR uma ordem de liquidação com status CREATED ");
    }

    @Override
    public Mono<SecondaryOrder> cancel(Order order, SecondaryOrder secondaryOrder) {
        throw new AmeException(400, "release_status_error", "Não é possível CANCELAR uma ordem de liquidação com status CREATED ");
    }




}