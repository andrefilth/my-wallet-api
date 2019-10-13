package com.amedigital.wallet.config;

import com.amedigital.wallet.endoint.converter.method.*;
import com.amedigital.wallet.endoint.converter.order.primary.*;
import com.amedigital.wallet.endoint.converter.order.secondary.CashBackRequestConverter;
import com.amedigital.wallet.endoint.converter.order.secondary.RefundRequestConverter;
import com.amedigital.wallet.endoint.converter.order.secondary.ReleaseRequestConverter;
import com.amedigital.wallet.endoint.converter.order.secondary.RouterSecondaryOrderRequestConverter;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.service.impl.order.GiftCashInOrderStateService;
import com.amedigital.wallet.service.state.order.giftcashin.AuthorizedGiftCashInOrderState;
import com.amedigital.wallet.service.state.order.giftcashin.CancelledGiftCashInOrderState;
import com.amedigital.wallet.service.state.order.giftcashin.CapturedGiftCashInOrderState;
import com.amedigital.wallet.service.state.order.giftcashin.CreatedGiftCashInOrderState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

@Configuration
public class WalletConfig {

    @Bean
    public DataBufferFactory dataBufferFactory() {
        return new DefaultDataBufferFactory();
    }

    @Bean
    public AmeInternalException ameInternalException() {
        return new AmeInternalException();
    }

    @Bean
    public CashMethodRequestConverter cashMethodRequestConverter() {
        return new CashMethodRequestConverter();
    }

    @Bean
    public CreditCardMethodRequestConverter creditCardMethodRequestConverter() {
        return new CreditCardMethodRequestConverter();
    }

    @Bean
    public BankTransferMethodRequestConverter bankTransferMethodRequestConverter() {
        return new BankTransferMethodRequestConverter();
    }

    @Bean
    public RouterMethodRequestConverter routerMethodRequestConverter(CreditCardMethodRequestConverter creditCardMethodRequestConverter,
                                                                     CashMethodRequestConverter cashMethodRequestConverter,
                                                                     BankTransferMethodRequestConverter bankTransferMethodRequestConverter,
                                                                     CashBackMethodRequestConverter cashBackMethodRequestConverter) {
        return new RouterMethodRequestConverter(creditCardMethodRequestConverter, cashMethodRequestConverter, bankTransferMethodRequestConverter, cashBackMethodRequestConverter);
    }

    @Bean
    public PurchaseOrderRequestConverter purchaseOrderRequestConverter(RouterMethodRequestConverter routerMethodRequestConverter) {
        return new PurchaseOrderRequestConverter(routerMethodRequestConverter);
    }

    @Bean
    public CashInOrderRequestConverter cashInOrderRequestConverter(RouterMethodRequestConverter methodConverter) {
        return new CashInOrderRequestConverter(methodConverter);
    }

    @Bean
    public TransferBetweenWalletsOrderRequestConverter transferBetweenWalletsOrderRequestConverter(RouterMethodRequestConverter methodConverter) {
        return new TransferBetweenWalletsOrderRequestConverter(methodConverter);
    }

    @Bean
    public CashOutOrderRequestConverter cashOutOrderRequestConverter(RouterMethodRequestConverter methodConverter) {
        return new CashOutOrderRequestConverter(methodConverter);
    }

    @Bean
    public ReleaseRequestConverter releaseOrderRequestConverter() {
        return new ReleaseRequestConverter();
    }

    @Bean
    public CashBackRequestConverter cashBackOrderRequestConverter() {
        return new CashBackRequestConverter();
    }

    @Bean
    public GiftCashInRequestConverter giftCashInRequestConverter() {
        return new GiftCashInRequestConverter();
    }

    @Bean
    public RouterPrimaryOrderRequestConverter routerOrderRequestConverter(PurchaseOrderRequestConverter purchaseConverter,
                                                                          CashInOrderRequestConverter cashInOrderRequestConverter,
                                                                          TransferBetweenWalletsOrderRequestConverter transferBetweenWalletsOrderRequestConverter,
                                                                          GiftCashInRequestConverter giftCashInRequestConverter,
                                                                          StoreCashInOrderRequestConverter storeCashInOrderRequestConverter,
                                                                          StoreCashOutOrderRequestConverter storeCashOutOrderRequestConverter,
                                                                          CashOutOrderRequestConverter cashOutOrderRequestConverter,
                                                                          BankCashInOrderRequestConverter bankCashInOrderRequestConverter) {

        return new RouterPrimaryOrderRequestConverter(
                purchaseConverter,
                cashInOrderRequestConverter,
                transferBetweenWalletsOrderRequestConverter,
                giftCashInRequestConverter,
                storeCashInOrderRequestConverter,
                storeCashOutOrderRequestConverter,
                cashOutOrderRequestConverter,
                bankCashInOrderRequestConverter);
    }

    @Bean
    public GiftCashInOrderStateService giftCashInOrderStateService(CreatedGiftCashInOrderState createdGiftCashInOrderState,
                                                                   AuthorizedGiftCashInOrderState authorizedGiftCashInOrderState,
                                                                   CapturedGiftCashInOrderState capturedGiftCashInOrderState,
                                                                   CancelledGiftCashInOrderState cancelledGiftCashInOrderState) {

        return new GiftCashInOrderStateService(createdGiftCashInOrderState, authorizedGiftCashInOrderState,
                cancelledGiftCashInOrderState, capturedGiftCashInOrderState);
    }

    @Bean
    public RouterSecondaryOrderRequestConverter routerSecondaryOrderRequestConverter(RefundRequestConverter refundRequestConverter,
                                                                                     ReleaseRequestConverter releaseRequestConverter,
                                                                                     CashBackRequestConverter cashBackRequestConverter) {

        return new RouterSecondaryOrderRequestConverter(refundRequestConverter, releaseRequestConverter, cashBackRequestConverter);

    }
}