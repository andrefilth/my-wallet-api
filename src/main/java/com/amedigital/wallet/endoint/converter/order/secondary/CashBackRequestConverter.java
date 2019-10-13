package com.amedigital.wallet.endoint.converter.order.secondary;

import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.request.order.CashBackOrderRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.secondary.CashbackOrder;
import com.amedigital.wallet.model.order.secondary.ReleaseOrder;
import com.amedigital.wallet.util.TransactionUtil;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

public class CashBackRequestConverter implements SecondaryOrderRequestConverter<CashBackOrderRequest, CashbackOrder> {

    @Override
    public CashbackOrder from(Order order, CashBackOrderRequest orderRequest, RequestContext context) {

        Wallet wallet = context.getTokenWallet()
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet da sessão."));

        ZonedDateTime now = ZonedDateTime.now();

        String orderUuid = UUID.randomUUID().toString();

        String secondaryId = context.getSecondaryId()
                .orElseThrow(() -> new AmeInvalidInputException("refund_error", "Id do estorno é um campo obrigatório"));

        return new CashbackOrder.Builder(order.getUuid())
                .setUuid(orderUuid)
                .setNsu(TransactionUtil.createNsu())
                .setStatus(OrderStatus.CREATED)
                .setCreatedByWalletId(wallet.getId().get())
                .setTotalAmountInCents(orderRequest.getTotalAmountInCents())
                .setTransactions(Collections.emptyList())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setSecondaryId(secondaryId)
                .build();  }
}
