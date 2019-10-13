package com.amedigital.wallet.endoint.converter.order.primary;

import com.amedigital.wallet.constants.enuns.AuthorizationMethod;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.method.RouterMethodRequestConverter;
import com.amedigital.wallet.endoint.request.order.StoreCashInOrderRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.primary.StoreCashInOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.util.TransactionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StoreCashInOrderRequestConverter implements PrimaryRequestConverter<StoreCashInOrderRequest, StoreCashInOrder> {

    private final RouterMethodRequestConverter methodConverter;

    @Autowired
    public StoreCashInOrderRequestConverter(RouterMethodRequestConverter methodConverter) {
        this.methodConverter = methodConverter;
    }

    @Override
    public StoreCashInOrder from(StoreCashInOrderRequest orderRequest, RequestContext context) {
        Long walletId = context.getTokenWallet().flatMap(Wallet::getId)
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet da sessão."));

        ZonedDateTime now = ZonedDateTime.now();

        String orderUuid = UUID.randomUUID().toString();

        List<Transaction> transactions = orderRequest.getPaymentMethods()
                .stream()
                .map(paymentMethodRequest -> methodConverter.from(paymentMethodRequest, context))
                .collect(Collectors.toList());

        return new StoreCashInOrder.Builder()
                .setCreditWalletUUID(orderRequest.getCreditWalletId())
                .setCreatedByWalletId(walletId)
                .setUuid(orderUuid)
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