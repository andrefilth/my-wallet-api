package com.amedigital.wallet.converters;

import com.amedigital.wallet.constants.enuns.CreditCardStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.service.atom.request.CreditCardParameter;
import com.amedigital.wallet.service.atom.request.PaymentItem;
import com.amedigital.wallet.service.atom.request.PaymentParameter;
import com.amedigital.wallet.service.atom.response.PaymentPresenter;
import com.amedigital.wallet.service.atom.response.enums.Currency;
import com.amedigital.wallet.service.atom.response.enums.InstallmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Collections;

import static com.amedigital.wallet.util.MapperUtil.toZonedDateTime;

public class AuthorizationConverter {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationConverter.class);

    public static PaymentParameter toPaymentParameter(CreditCardTransaction transaction, String walletUuid) {
        LOG.info("Convertendo [{}] com a walletId [{}] para criar o payload de autorização no ATOM", transaction, walletUuid);

        CreditCardParameter creditCardParameter = CreditCardParameter.builder()
                .setSecurityCode(transaction.getCvv())
                .setVaultId(transaction.getCreditCardId())
                .setOwnerId(walletUuid)
                .build();

        PaymentItem paymentItem = PaymentItem.builder()
                .setPaymentReference(transaction.getGatewayPaymentReference())
                .setAmountInCents(transaction.getAmountInCents())
                .setInstallments(transaction.getNumberOfInstallments())
                .setCreditCard(creditCardParameter)
                .setCurrency(Currency.BRL)
                .setInstallmentType(transaction.getNumberOfInstallments().equals(1) ? InstallmentType.CASH_DOWN : InstallmentType.MERCHANT)
                .setBusinessUnit("b2b")
                .setSoftDescriptor("AME Digital")
                .build();

        return PaymentParameter.builder()
                .setOrderReference(transaction.getOrderUuid())
                .setPayments(Collections.singletonList(paymentItem))
                .build();
    }

    public static CreditCardTransaction toCreditCardTransaction(CreditCardTransaction transaction, PaymentPresenter paymentPresenter) {
        LOG.info("Convertendo a resposta de autorização do ATOM [{}]", paymentPresenter);

        CreditCardTransaction.Builder builder = transaction.copy()
                .setCreditCardStatus(CreditCardStatus.valueOf(paymentPresenter.getState().name()))
                .setAuthorizationCode(paymentPresenter.getAuthorizationCode())
                .setAcquirer(paymentPresenter.getAcquirer().name())
                .setTid(paymentPresenter.getTid())
                .setNsu(paymentPresenter.getNsu())
                .setAuthorizationNsu(paymentPresenter.getNsu())
                .setAuthorizationCode(paymentPresenter.getAuthorizationCode())
                .setAuthorizationTid(paymentPresenter.getTid())
                .setHolderName(paymentPresenter.getCreditCard().getHolderName())
                .setMaskedNumber(paymentPresenter.getCreditCard().getMaskedNumber())
                .setBrand(paymentPresenter.getCreditCard().getBrand().name())
                .setExpirationMonth(paymentPresenter.getCreditCard().getExpirationMonth())
                .setExpirationYear(paymentPresenter.getCreditCard().getExpirationYear())
                .setCurrency(paymentPresenter.getCurrency().name())
                .setAuthorizationDate(paymentPresenter.getAuthorizationDate() == null ? null : toZonedDateTime(paymentPresenter.getAuthorizationDate()));

        switch (paymentPresenter.getState()) {
            case AUTHORIZED:
                builder.setStatus(TransactionStatus.AUTHORIZED)
                        .setGatewayResponseMessage(paymentPresenter.getResponseMessage())
                        .setGatewayResponseCode(paymentPresenter.getResponseCode());
                break;
            case UNAUTHORIZED:
                builder.setStatus(TransactionStatus.DENIED)
                        .setGatewayResponseCode(paymentPresenter.getErrorCode())
                        .setGatewayResponseMessage(paymentPresenter.getErrorMessage());
                break;
            case ERROR_TO_AUTHORIZE:
                builder.setStatus(TransactionStatus.ERROR)
                        .setGatewayResponseCode(paymentPresenter.getErrorCode())
                        .setGatewayResponseMessage(paymentPresenter.getErrorMessage());
                break;
        }

        builder.setUpdatedAt(ZonedDateTime.now());

        return builder.build();
    }

}
