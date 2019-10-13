package com.amedigital.wallet.repository;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.model.StatementItem;
import com.amedigital.wallet.model.TransactionDataItem;
import reactor.core.publisher.Flux;

import java.time.ZonedDateTime;
import java.util.List;

public interface StatementRepository {

    Flux<StatementItem> getWalletStatement(long walletId, int size, int offset, List<TransactionType> types, List<OrderStatus> statusList);

    Flux<TransactionDataItem> findTransactionsDataBy(OrderStatus orderStatus,
                                                     OrderType orderType,
                                                     String walletId,
                                                     ZonedDateTime dateStart,
                                                     ZonedDateTime dateEnd);
}
