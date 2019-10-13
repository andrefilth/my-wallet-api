package com.amedigital.wallet.service;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.constants.enuns.TransactionType;
import com.amedigital.wallet.endoint.request.TransactionDataRequest;
import com.amedigital.wallet.model.StatementItem;
import com.amedigital.wallet.model.TransactionDataItem;
import reactor.core.publisher.Flux;

import java.util.List;

public interface StatementService {

    Flux<StatementItem> getWalletStatement(final long walletId, final int size, final int offset, List<TransactionType> type, List<OrderStatus> statusList);

    Flux<TransactionDataItem> transactionData(final TransactionDataRequest request);
}
