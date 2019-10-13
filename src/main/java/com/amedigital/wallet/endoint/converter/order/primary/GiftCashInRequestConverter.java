package com.amedigital.wallet.endoint.converter.order.primary;

import com.amedigital.wallet.constants.Constants;
import com.amedigital.wallet.constants.enuns.AuthorizationMethod;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.request.order.GiftCashInOrderRequest;
import com.amedigital.wallet.model.order.primary.GiftCashInOrder;
import com.amedigital.wallet.util.TransactionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.UUID;

import static com.amedigital.wallet.util.ValidatorUtil.*;
import static java.time.ZonedDateTime.now;

public class GiftCashInRequestConverter implements PrimaryRequestConverter<GiftCashInOrderRequest, GiftCashInOrder> {

    @Autowired
    public GiftCashInRequestConverter() {

    }

    @Override
    public GiftCashInOrder from(GiftCashInOrderRequest giftCashInOrderRequest, RequestContext context) {
        validate(giftCashInOrderRequest);
        ZonedDateTime now = now();

        return new GiftCashInOrder.Builder()
                .setCreatedByWalletId(Constants.DEFAULT_MANAGER_WALLET_ID)
                .setUuid(UUID.randomUUID().toString())
                .setNsu(TransactionUtil.createNsu())
                .setTitle(giftCashInOrderRequest.getTitle())
                .setDescription(giftCashInOrderRequest.getDescription())
                .setCustomPayload(giftCashInOrderRequest.getCustomPayload())
                .setAuthorizationMethod(AuthorizationMethod.NONE)
                .setAmountPerWalletInCents(giftCashInOrderRequest.getAmountPerWalletInCents())
                .setCustomerWalletIds(giftCashInOrderRequest.getCustomersWalletIds())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();
    }

    private void validate(GiftCashInOrderRequest giftCashInOrderRequest) {
        notNull(giftCashInOrderRequest.getType(), "Type");
        notEmpty(giftCashInOrderRequest.getTitle(), "Title");
        notEmpty(giftCashInOrderRequest.getDescription(), "Description");
        notNull(giftCashInOrderRequest.getAmountPerWalletInCents(), "AmountPerWalletInCents");
        notEmpty(giftCashInOrderRequest.getCustomersWalletIds(), e -> !e.isBlank(), "CustomerWalletIds");
        unused(giftCashInOrderRequest.getTotalAmountInCents(), "TotalAmountInCents");
        unused(giftCashInOrderRequest.getPaymentMethods(), "PaymentMethods");
        unused(giftCashInOrderRequest.getCustomPayload(), "CustomPayload");
        unused(giftCashInOrderRequest.getCreatedByWalletId(), "CreatedByWalletId");
    }
}
