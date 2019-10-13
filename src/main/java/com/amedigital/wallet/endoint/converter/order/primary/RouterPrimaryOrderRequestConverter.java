package com.amedigital.wallet.endoint.converter.order.primary;

import com.amedigital.wallet.constants.enuns.OrderType;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.request.order.*;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.order.PrimaryOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class RouterPrimaryOrderRequestConverter implements PrimaryRequestConverter<OrderRequest, PrimaryOrder> {

    private final PurchaseOrderRequestConverter purchaseConverter;
    private final CashInOrderRequestConverter cashInOrderRequestConverter;
    private final TransferBetweenWalletsOrderRequestConverter transferBetweenWalletsOrderRequestConverter;
    private final GiftCashInRequestConverter giftCashInRequestConverter;
    private final CashOutOrderRequestConverter cashOutOrderRequestConverter;
    private final StoreCashInOrderRequestConverter storeCashInOrderRequestConverter;
    private final StoreCashOutOrderRequestConverter storeCashOutOrderRequestConverter;
    private final BankCashInOrderRequestConverter bankCashInOrderRequestConverter;

    @Autowired
    public RouterPrimaryOrderRequestConverter(PurchaseOrderRequestConverter purchaseConverter,
                                              CashInOrderRequestConverter cashInOrderRequestConverter,
                                              TransferBetweenWalletsOrderRequestConverter transferBetweenWalletsOrderRequestConverter,
                                              GiftCashInRequestConverter giftCashInRequestConverter,
                                              StoreCashInOrderRequestConverter storeCashInOrderRequestConverter,
                                              StoreCashOutOrderRequestConverter storeCashOutOrderRequestConverter,
                                              CashOutOrderRequestConverter cashOutOrderRequestConverter,
                                              BankCashInOrderRequestConverter bankCashInOrderRequestConverter) {

        this.purchaseConverter = purchaseConverter;
        this.cashInOrderRequestConverter = cashInOrderRequestConverter;
        this.transferBetweenWalletsOrderRequestConverter = transferBetweenWalletsOrderRequestConverter;
        this.giftCashInRequestConverter = giftCashInRequestConverter;
        this.cashOutOrderRequestConverter = cashOutOrderRequestConverter;
        this.storeCashInOrderRequestConverter = storeCashInOrderRequestConverter;
        this.storeCashOutOrderRequestConverter = storeCashOutOrderRequestConverter;
        this.bankCashInOrderRequestConverter = bankCashInOrderRequestConverter;
    }

    @Override
    public PrimaryOrder from(OrderRequest order, RequestContext context) {
        OrderType type = order.getType();

        switch (type) {
            case PURCHASE:
                return purchaseConverter.from((PurchaseOrderRequest) order, context);
            case CASH_IN:
                return cashInOrderRequestConverter.from((CashInOrderRequest) order, context);
            case TRANSFER_BETWEEN_WALLETS:
                return transferBetweenWalletsOrderRequestConverter.from((TransferBetweenWalletsOrderRequest) order, context);
            case GIFT_CASH_IN:
                return giftCashInRequestConverter.from((GiftCashInOrderRequest) order, context);
            case STORE_CASH_IN:
                return storeCashInOrderRequestConverter.from((StoreCashInOrderRequest) order, context);
            case STORE_CASH_OUT:
            	return storeCashOutOrderRequestConverter.from((StoreCashOutOrderRequest) order, context);
            case CASH_OUT:
            	return cashOutOrderRequestConverter.from((CashOutOrderRequest) order, context);
            case BANK_CASH_IN:
            	return bankCashInOrderRequestConverter.from((BankCashInOrderRequest) order, context);
            default:
                throw new AmeInternalException("O tipo de ordem não é suportada.");
        }
    }
}