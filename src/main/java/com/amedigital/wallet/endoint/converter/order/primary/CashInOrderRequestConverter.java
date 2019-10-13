package com.amedigital.wallet.endoint.converter.order.primary;

import com.amedigital.wallet.constants.enuns.AuthorizationMethod;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.method.RouterMethodRequestConverter;
import com.amedigital.wallet.endoint.request.order.CashInOrderRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.primary.CashInOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.util.TransactionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CashInOrderRequestConverter implements PrimaryRequestConverter<CashInOrderRequest, CashInOrder> {

    private final RouterMethodRequestConverter methodConverter;

    @Autowired
    public CashInOrderRequestConverter(RouterMethodRequestConverter methodConverter) {
        this.methodConverter = methodConverter;
    }

    @Override
    public CashInOrder from(CashInOrderRequest cashInOrderRequest, RequestContext context) {
        Wallet wallet = context.getTokenWallet()
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet da sessão."));

        ZonedDateTime now = ZonedDateTime.now();

        String orderUuid = UUID.randomUUID().toString();

        List<Transaction> transactions = cashInOrderRequest.getPaymentMethods()
                .stream()
                .map(paymentMethodRequest -> methodConverter.from(paymentMethodRequest, context))
                .collect(Collectors.toList());

        return new CashInOrder.Builder()
                .setCreatedByWalletId(wallet.getId().get())
                .setUuid(orderUuid)
                .setNsu(TransactionUtil.createNsu())
                .setTitle(cashInOrderRequest.getTitle())
                .setDescription(cashInOrderRequest.getDescription())
                .setTotalAmountInCents(cashInOrderRequest.getTotalAmountInCents())
                .setTransactions(transactions)
                .setCustomPayload(cashInOrderRequest.getCustomPayload())
                .setAuthorizationMethod(AuthorizationMethod.QRCODE)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();
    }
}
