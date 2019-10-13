package com.amedigital.wallet.service.state.order.bankcashin;

import static com.amedigital.wallet.constants.Constants.BANK_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.authorizedMessageException;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.primary.BankCashInOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.DynamoRepository;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.service.paymentmethod.PaymentMethodRouter;
import com.amedigital.wallet.service.state.OrderState;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AuthorizedBankCashInOrderState implements OrderState<BankCashInOrder>{
	
    private final PaymentMethodRouter router;
    private final OrderRepository orderRepository;
    private final DynamoRepository dynamoRepository;

    @Autowired
    public AuthorizedBankCashInOrderState(PaymentMethodRouter router, OrderRepository orderRepository,DynamoRepository dynamoRepository) {
        this.router = router;
        this.orderRepository = orderRepository;
        this.dynamoRepository = dynamoRepository;
    }
	
	@Override
	public Mono<BankCashInOrder> create(BankCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                authorizedMessageException("criar", BANK_CASH_IN_ORDER_TYPE));
	}

	@Override
	public Mono<BankCashInOrder> authorize(BankCashInOrder order) {
		  return Mono.just(order);
	}

	@Override
	public Mono<BankCashInOrder> capture(BankCashInOrder order) {
	       return Flux.fromStream(order.getTransactions().stream())
	                .flatMap(router::capture)
	                .collectList()
	                .map(trs -> {
	                    var status = (trs.stream().allMatch(e -> e.getStatus().equals(TransactionStatus.CAPTURED))) ? OrderStatus.CAPTURED : OrderStatus.AUTHORIZED;
	                    var newOrder = new Action.Builder().setParentId(order.getAction().getId()).setType(ActionType.CAPTURE).setCreatedAt(ZonedDateTime.now()).build();
	                    return (Order) order.copy().setStatus(status).setTransactions(trs).setAction(newOrder).build();
	                })
	                .flatMap(orderRepository::save)
	                .map(o -> (BankCashInOrder) o)
	                .flatMap(tw -> dynamoRepository.save(order.getOrderDetailUuid(), order.getCustomPayload()).map(t -> tw));
	}

	@Override
	public Mono<BankCashInOrder> cancel(BankCashInOrder order) {
        return Flux.fromStream(order.getTransactions().stream())
                .flatMap(router::cancel)
                .collectList()
                .map(transactions -> setOrderStatus(transactions, order))
                .flatMap(orderRepository::save)
                .cast(BankCashInOrder.class);
	}

    private BankCashInOrder setOrderStatus(List<Transaction> transactions, BankCashInOrder order) {
    	
        Action action = new Action.Builder()
                .setCreatedAt(ZonedDateTime.now())
                .setType(ActionType.CANCEL)
                .setParentId(order.getAction().getId())
                .build();

        BankCashInOrder.Builder builder = order.copy()
                .setTransactions(transactions)
                .setCreatedAt(order.getCreatedAt())
                .setAction(action);

        var cancelledTransactions = transactions
                .stream()
                .allMatch(t -> TransactionStatus.CANCELLED.equals(t.getStatus()));

        if (cancelledTransactions) {
            builder.setStatus(OrderStatus.CANCELLED);
        }

        return builder.build();
    }
}
