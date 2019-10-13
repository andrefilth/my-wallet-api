package com.amedigital.wallet.endoint.converter.order.primary;

import com.amedigital.wallet.constants.enuns.AuthorizationMethod;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.method.RouterMethodRequestConverter;
import com.amedigital.wallet.endoint.request.order.PurchaseOrderRequest;
import com.amedigital.wallet.model.order.primary.PurchaseOrder;
import com.amedigital.wallet.model.transaction.Transaction;
import com.amedigital.wallet.util.TransactionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PurchaseOrderRequestConverter implements PrimaryRequestConverter<PurchaseOrderRequest, PurchaseOrder> {

    private final RouterMethodRequestConverter methodConverter;

    @Autowired
    public PurchaseOrderRequestConverter(RouterMethodRequestConverter methodConverter) {
        this.methodConverter = methodConverter;
    }

    @Override
    public PurchaseOrder from(PurchaseOrderRequest orderRequest, RequestContext requestContext) {
        ZonedDateTime now = ZonedDateTime.now();

        String orderUuid = UUID.randomUUID().toString();

        List<Transaction> transactions = orderRequest.getPaymentMethods()
                .stream()
                .map(methodRequest -> methodConverter.from(methodRequest, requestContext))
                .collect(Collectors.toList());

        return new PurchaseOrder.Builder()
                .setCreatedByWalletUuid(orderRequest.getCreatedByWalletId())
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
                .build();
    }
}