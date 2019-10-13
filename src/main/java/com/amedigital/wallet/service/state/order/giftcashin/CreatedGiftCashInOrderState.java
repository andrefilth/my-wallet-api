package com.amedigital.wallet.service.state.order.giftcashin;

import com.amedigital.wallet.constants.Constants;
import com.amedigital.wallet.constants.enuns.*;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.GiftCashInOrder;
import com.amedigital.wallet.model.transaction.CashTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.repository.WalletRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.amedigital.wallet.constants.Constants.DEFAULT_MANAGER_WALLET_ID;
import static com.amedigital.wallet.constants.Constants.GIFT_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.cancelledMessageException;
import static com.amedigital.wallet.constants.Messages.capturedMessageException;
import static com.amedigital.wallet.constants.enuns.CashStatus.CREATED;
import static java.util.UUID.randomUUID;

@Service
public class CreatedGiftCashInOrderState implements OrderState<GiftCashInOrder> {

    private final OrderRepository orderRepository;
    private final PaymentMethodRouter paymentMethodRouter;
    private final WalletRepository walletRepository;

    @Autowired
    public CreatedGiftCashInOrderState(OrderRepository orderRepository,
                                       PaymentMethodRouter paymentMethodRouter,
                                       WalletRepository walletRepository) {
        this.orderRepository = orderRepository;
        this.paymentMethodRouter = paymentMethodRouter;
        this.walletRepository = walletRepository;
    }

    private List<Transaction> cashTransaction(Long walletId, long amountPerWalletInCents) {
        final var now = ZonedDateTime.now();
        
        final var credit = new CashTransaction.Builder()
                .setWalletId(walletId)
                .setAmountInCents(amountPerWalletInCents)
                .setCashStatus(CREATED)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setCashCreatedAt(now)
                .setCashUpdatedAt(now)
                .setUuid(randomUUID().toString())
                .setStatus(TransactionStatus.CREATED)
                .setLatest(true)
                .setType(TransactionType.CREDIT)
                .setPeerWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
                .build();

        final var debit = credit.copy()
                .setUuid(randomUUID().toString())
                .setWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
                .setType(TransactionType.DEBIT)
                .setPeerWalletId(walletId)
                .build();

        return Arrays.asList(credit, debit);
    }

    @Override
    public Mono<GiftCashInOrder> create(final GiftCashInOrder order) {

        final var orderDetailUuid = randomUUID().toString();
        return Flux.fromStream(order.getCustomerWalletIds().stream())
                .flatMap(walletRepository::findByUuid)
                .map(Wallet::getId)
                .flatMap(Mono::justOrEmpty)
                .map(walletId1 -> cashTransaction(walletId1, order.getAmountPerWalletInCents()))
                .flatMap(Flux::fromIterable)
                .collectList()
                .map(transactions -> order.copy()
                        .setCreatedByWalletId(DEFAULT_MANAGER_WALLET_ID)
                        .setTotalAmountInCents(creditAmountFrom(transactions))
                        .setStatus(OrderStatus.CREATED)
                        .setAction(createAction())
                        .setAuthorizationMethod(AuthorizationMethod.NONE)
                        .setOrderDetailUuid(orderDetailUuid)
                        .setTransactions(transactions)
                        .setOrderDetailUuid(randomUUID().toString())
                        .build())
                .flatMap(orderRepository::save)
                .cast(GiftCashInOrder.class)
                .flatMap(this::authorize);
    }

    private Long creditAmountFrom(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> TransactionType.CREDIT == t.getType())
                .collect(Collectors.toList())
                .stream()
                .map(Transaction::getAmountInCents)
                .reduce(0L, (x, y) -> x + y);
    }

    @Override
    public Mono<GiftCashInOrder> authorize(GiftCashInOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(paymentMethodRouter::authorize)
                .collectList()
                .map(li -> setOrderStatus(li, order))
                .flatMap(orderRepository::save)
                .cast(GiftCashInOrder.class);
    }

    @Override
    public Mono<GiftCashInOrder> capture(GiftCashInOrder order) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                capturedMessageException("capturar", GIFT_CASH_IN_ORDER_TYPE)));
    }

    @Override
    public Mono<GiftCashInOrder> cancel(GiftCashInOrder order) {
        return Mono.error(new AmeInvalidInputException("wallet_validation",
                cancelledMessageException("cancelar", GIFT_CASH_IN_ORDER_TYPE)));
    }

    private Action createAction() {
        return new Action.Builder()
                .setType(ActionType.CREATE)
                .setCreatedAt(ZonedDateTime.now())
                .build();
    }

    private Action authorizeActionFor(GiftCashInOrder order) {
        return new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.AUTHORIZE)
                .setParentId(order.getAction().getId())
                .build();
    }

    private GiftCashInOrder setOrderStatus(List<Transaction> transactions, GiftCashInOrder order) {

        final var giftcashInOrder = order.copy()
                .setStatus(OrderStatus.AUTHORIZED)
                .setTransactions(transactions)
                .setAction(authorizeActionFor(order))
                .setCreatedAt(order.getCreatedAt())
                .build();

        return giftcashInOrder.getTransactions()
                .stream()
                .filter(t -> TransactionStatus.DENIED.equals(t.getStatus()) || TransactionStatus.ERROR.equals(t.getStatus()))
                .findFirst()
                .map(transaction -> giftcashInOrder.copy().setStatus(OrderStatus.DENIED).build())
                .orElse(giftcashInOrder);
    }
}
