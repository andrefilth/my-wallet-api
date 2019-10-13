package com.amedigital.wallet.service.state.order.cashback.purchase;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.CashBackStatus;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.model.transaction.CashBackTransaction;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.SecondaryOrderState;
import com.amedigital.wallet.util.TransactionUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.amedigital.wallet.constants.enuns.ActionType.CREATE;
import static com.amedigital.wallet.constants.enuns.TransactionType.CREDIT;
import static com.amedigital.wallet.constants.enuns.TransactionType.DEBIT;

@Service
public class CreatedPurchaseCashbackState implements SecondaryOrderState<PurchaseOrder, CashbackOrder> {

    private final OrderRepository repository;
    private final PaymentMethodRouter paymentMethodRouter;

    @Autowired
    public CreatedPurchaseCashbackState(OrderRepository repository, PaymentMethodRouter paymentMethodRouter) {
        this.repository = repository;
        this.paymentMethodRouter = paymentMethodRouter;
    }

    @Override
    public Mono<CashbackOrder> create(PurchaseOrder order, CashbackOrder secondaryOrder) {
        var now = ZonedDateTime.now();
        Action action = new Action.Builder()
                .setType(CREATE)
                .setCreatedAt(now)
                .build();

        Long customerWalletId = order.getTransactions().get(0).getWalletId();

        var debit = CashBackTransaction
                .builder()
                .setUuid(UUID.randomUUID().toString())
                .setPeerWalletId(customerWalletId)
                .setWalletId(order.getCreatedByWalletId())
                .setAmountInCents(secondaryOrder.getTotalAmountInCents())
                .setCashStatus(CashBackStatus.CREATED)
                .setCashUpdatedAt(now)
                .setCashCreatedAt(now)
                .setOrderUuid(order.getUuid())
                .setStatus(TransactionStatus.CREATED)
                .setType(DEBIT)
                .setLatest(true)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();

        var credit = debit.copy()
                .setWalletId(customerWalletId)
                .setPeerWalletId(order.getCreatedByWalletId())
                .setUuid(UUID.randomUUID().toString())
                .setType(CREDIT)
                .build();

        var transactions = Lists.newArrayList(debit, credit).stream().map(t -> (Transaction) t).collect(Collectors.toList());


        var cashBackOrder = (CashbackOrder) secondaryOrder.copy()
                .setUuid(UUID.randomUUID().toString())
                .setNsu(TransactionUtil.createNsu())
                .setStatus(OrderStatus.CREATED)
                .setTransactions(transactions)
                .setAction(action)
                .setReferenceOrderUuid(order.getUuid())
                .setTitle(order.getTitle())
                .setAuthorizationMethod(order.getAuthorizationMethod())
                .setDescription(order.getDescription())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();

        return repository.save(cashBackOrder).cast(CashbackOrder.class).flatMap(sord -> authorize(order, sord));

    }

    @Override
    public Mono<CashbackOrder> authorize(PurchaseOrder order, CashbackOrder secondaryOrder) {
        return Flux.fromStream(secondaryOrder.getTransactions().stream())
                .flatMap(paymentMethodRouter::authorize)
                .collectList()
                .map(li -> setOrderStatus(li, secondaryOrder))
                .flatMap(repository::save)
                .map(o -> (CashbackOrder) o);

    }

    private CashbackOrder setOrderStatus(List<Transaction> transactions, CashbackOrder order) {
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.AUTHORIZE)
                .setParentId(order.getAction().getId())
                .build();

        CashbackOrder cashbackOrder = order.copy()
                .setStatus(OrderStatus.AUTHORIZED)
                .setTransactions(transactions)
                .setAction(action)
                .setCreatedAt(order.getCreatedAt())
                .build();

        return cashbackOrder.getTransactions()
                .stream()
                .filter(t -> TransactionStatus.DENIED.equals(t.getStatus()) || TransactionStatus.ERROR.equals(t.getStatus()))
                .findFirst()
                .map(transaction -> cashbackOrder.copy().setStatus(OrderStatus.DENIED).build())
                .orElse(cashbackOrder);
    }

    @Override
    public Mono<CashbackOrder> finish(PurchaseOrder order, CashbackOrder secondaryOrder) {
        throw new AmeException(400, "release_status_error", "Não é possível CAPTURAR uma ordem de cashback com status CREATED ");

    }

	@Override
	public Mono<CashbackOrder> cancel(PurchaseOrder order, CashbackOrder secondaryOrder) {
		 throw new AmeException(400, "release_status_error", "Não é possível CAPTURAR uma ordem de cashback com status CREATED ");
	}
}
