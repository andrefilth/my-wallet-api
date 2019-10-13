package com.amedigital.wallet.endoint.converter.order.primary;

import com.amedigital.wallet.constants.enuns.AuthorizationMethod;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.method.RouterMethodRequestConverter;
import com.amedigital.wallet.endoint.request.order.TransferBetweenWalletsOrderRequest;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.Wallet;
import com.amedigital.wallet.model.order.primary.TransferBetweenWalletsOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.util.TransactionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

    public class TransferBetweenWalletsOrderRequestConverter implements PrimaryRequestConverter<TransferBetweenWalletsOrderRequest, TransferBetweenWalletsOrder> {

    private final RouterMethodRequestConverter methodConverter;

    @Autowired
    public TransferBetweenWalletsOrderRequestConverter(RouterMethodRequestConverter methodConverter) {
        this.methodConverter = methodConverter;
    }

    @Override
    public TransferBetweenWalletsOrder from(TransferBetweenWalletsOrderRequest orderRequest, RequestContext context) {
        Wallet wallet = context.getTokenWallet()
                .orElseThrow(() -> new AmeInvalidInputException("invalid_wallet_token_id", "Não foi possível encontrar a wallet da sessão."));

        ZonedDateTime now = ZonedDateTime.now();

        String orderUuid = UUID.randomUUID().toString();

        List<Transaction> transactions = orderRequest.getPaymentMethods()
                .stream()
                .map(paymentMethodRequest -> methodConverter.from(paymentMethodRequest, context))
                .collect(Collectors.toList());

        return new TransferBetweenWalletsOrder.Builder()
                .setCreatedByWalletId(wallet.getId().get())
                .setToWalletUuid(orderRequest.getCreditWalletId())
                .setUuid(orderUuid)
                .setNsu(TransactionUtil.createNsu())
                .setTitle(orderRequest.getTitle())
                .setDescription(orderRequest.getDescription())
                .setTotalAmountInCents(orderRequest.getTotalAmountInCents())
                .setTransactions(transactions)
                .setCustomPayload(orderRequest.getCustomPayload())
                .setAuthorizationMethod(AuthorizationMethod.QRCODE)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setToOwnerUuid(orderRequest.getToOwnerId())
                .build();
    }
}