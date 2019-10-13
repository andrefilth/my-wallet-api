package com.amedigital.wallet.service.impl;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.endoint.request.TransactionDataRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.StatementItem;
import com.amedigital.wallet.model.TransactionDataItem;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.repository.OrderRepository;
import com.amedigital.wallet.repository.StatementRepository;
import com.amedigital.wallet.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.amedigital.wallet.util.ValidatorUtil.notEmpty;
import static com.amedigital.wallet.util.ValidatorUtil.notNull;

@Service
public class StatementServiceImpl implements StatementService {

    private final StatementRepository statementRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public StatementServiceImpl(StatementRepository statementRepository, OrderRepository orderRepository) {
        this.statementRepository = statementRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Flux<StatementItem> getWalletStatement(final long walletId, final int size, final int offset, List<TransactionType> types, List<OrderStatus> statusList) {
        return statementRepository.getWalletStatement(walletId, size, offset, types, statusList)
                .flatMapSequential(t -> t.getType() == OrderType.GIFT_CASH_IN ? changeGiftCashInValue(t) : Mono.just(t)); //graças ao monoide do morango.
    }

    private Mono<TransactionDataRequest> validate(TransactionDataRequest model) {
        try {

            notNull(model, "request");
            notEmpty(model.getWalletId(), "walletId");
            notNull(model.getType(), "type");
            notNull(model.getStatus(), "status");

            return Mono.just(model);
        } catch (AmeInvalidInputException e) {
            return Mono.error(e);
        }
    }

    @Override
    public Flux<TransactionDataItem> transactionData(TransactionDataRequest transactionDataRequest) {

        return
                validate(transactionDataRequest)
                        .flatMapMany(request ->
                                statementRepository.findTransactionsDataBy(
                                        request.getStatus(),
                                        request.getType(),
                                        request.getWalletId(),
                                        request.getDateStart(),
                                        request.getDateEnd()));
    }


    /**
     * Este método altera o valor do gift cash para pegar o valor da transação e não da ordem, pois o valor da ordem é o valor total e não o que realmente o usuario recebeu.
     *
     * @param item
     * @return
     */
    private Mono<StatementItem> changeGiftCashInValue(StatementItem item) {
        return orderRepository.findByUuid(item.getId())
                .flatMap(t -> {
                    var amount = t.getTransactions()
                            .stream()
                            .filter(tr -> TransactionType.CREDIT == tr.getType())
                            .mapToLong(Transaction::getAmountInCents)
                            .findFirst().getAsLong();

                    return Mono.just(item.copy()
                            .setAmountInCents(amount)
                            .build());
                });
    }
}
