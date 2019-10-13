package com.amedigital.wallet.endoint.converter.order.primary;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amedigital.wallet.constants.enuns.AuthorizationMethod;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.method.RouterMethodRequestConverter;
import com.amedigital.wallet.endoint.request.order.StoreCashOutOrderRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.primary.StoreCashOutOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.util.TransactionUtil;

@Component
public class StoreCashOutOrderRequestConverter implements PrimaryRequestConverter<StoreCashOutOrderRequest, StoreCashOutOrder> {

    private final RouterMethodRequestConverter methodConverter;

    @Autowired
    public StoreCashOutOrderRequestConverter(RouterMethodRequestConverter methodConverter) {
        this.methodConverter = methodConverter;
    }

    @Override
    public StoreCashOutOrder from(StoreCashOutOrderRequest orderRequest, RequestContext context) {
        Long walletId = context.getTokenWallet().flatMap(Wallet::getId)
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet da sessão."));

        ZonedDateTime now = ZonedDateTime.now();

        List<Transaction> transactions = orderRequest.getPaymentMethods()
                .stream()
                .map(paymentMethodRequest -> methodConverter.from(paymentMethodRequest, context))
                .collect(Collectors.toList());

        return new StoreCashOutOrder.Builder()
                .setCreditWalletUUID(orderRequest.getCreditWalletId())
                .setDebitWalletUUID(orderRequest.getDebitWalletId())
                .setDebitWalletId(walletId)
                .setUuid(UUID.randomUUID().toString())
                .setNsu(TransactionUtil.createNsu())
                .setTitle(orderRequest.getTitle())
                .setDescription(orderRequest.getDescription())
                .setTotalAmountInCents(orderRequest.getTotalAmountInCents())
                .setTransactions(transactions)
                .setCustomPayload(orderRequest.getCustomPayload())
                .setAuthorizationMethod(AuthorizationMethod.NONE)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();
    }
}