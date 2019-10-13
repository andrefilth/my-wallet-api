package com.amedigital.wallet.endoint.converter.order.secondary;

import com.amedigital.wallet.constants.enuns.ActionType;
import com.amedigital.wallet.constants.enuns.AuthorizationMethod;
import com.amedigital.wallet.constants.enuns.OrderStatus;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.request.order.RefundOrderRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.Action;
import com.amedigital.wallet.model.order.Order;
import com.amedigital.wallet.model.order.secondary.RefundOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.util.TransactionUtil;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RefundRequestConverter implements SecondaryOrderRequestConverter<RefundOrderRequest, RefundOrder> {


    @Override
    public RefundOrder from(Order order, RefundOrderRequest orderRequest, RequestContext requestContext) {
        ZonedDateTime now = ZonedDateTime.now();

        String orderUuid = UUID.randomUUID().toString();

        Wallet wallet = requestContext.getTokenWallet()
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet no request."));

        String secondaryId = requestContext.getSecondaryId()
                .orElseThrow(() -> new AmeInvalidInputException("refund_error", "Id do estorno é um campo obrigatório"));

        List<Transaction> transactions = Collections.emptyList();

        return new RefundOrder.Builder(order.getUuid())
                .setSecondaryId(secondaryId)
                .setDescription(order.getDescription())
                .setOrderDetailUuid(orderUuid)
                .setStatus(OrderStatus.CREATED)
                .setTitle(order.getTitle())
                .setUuid(orderUuid)
                .setNsu(TransactionUtil.createNsu())
                .setAuthorizationMethod(AuthorizationMethod.NONE)
                .setAction(Action.builder()
                        .setCreatedAt(now)
                        .setType(ActionType.CREATE)
                        .build())
                .setTransactions(transactions)
                .setCreatedByWalletId(wallet.getId().get())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setTotalAmountInCents(orderRequest.getTotalAmountInCents())
                .build();
    }
}
