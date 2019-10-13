package com.amedigital.wallet.converters;

import com.amedigital.wallet.constants.enuns.CreditCardStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.service.atom.response.OrderPresenter;
import com.amedigital.wallet.service.atom.response.PaymentPresenter;
import com.amedigital.wallet.service.atom.response.enums.PaymentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

import static com.amedigital.wallet.util.MapperUtil.toZonedDateTime;

public class CaptureConverter {

    private static final Logger LOG = LoggerFactory.getLogger(CaptureConverter.class);

    public static CreditCardTransaction fromTransaction(CreditCardTransaction t, OrderPresenter op) {
        LOG.info("Convertendo a resposta de captura do ATOM [{}], [{}]", t, op);

        CreditCardTransaction.Builder builder = t.copy();

        PaymentPresenter p = op.getPayments().get(0);

        if (PaymentState.CAPTURED.equals(p.getState())) {
            return builder
                    .setCreditCardStatus(CreditCardStatus.CAPTURED)
                    .setStatus(TransactionStatus.CAPTURED)
                    .setNsu(p.getNsu())
                    .setCaptureDate(toZonedDateTime(p.getCaptureDate()))
                    .setGatewayResponseCode(p.getResponseCode())
                    .setGatewayResponseMessage(p.getResponseMessage())
                    .setCaptureTid(p.getTid())
                    .setCaptureNsu(p.getNsu())
                    .build();

        } else if (PaymentState.ERROR_TO_CAPTURE.equals(p.getState())) {
            return builder
                    .setCreditCardStatus(CreditCardStatus.ERROR_TO_CAPTURE)
                    .setStatus(TransactionStatus.ERROR)
                    .setNsu(p.getNsu())
                    .setCaptureDate(toZonedDateTime(p.getCaptureDate()))
                    .setGatewayResponseCode(p.getErrorCode())
                    .setGatewayResponseMessage(p.getErrorMessage())
                    .setCaptureTid(p.getTid())
                    .setCaptureNsu(p.getNsu())
                    .setUpdatedAt(ZonedDateTime.now())
                    .build();

        } else {
            return builder
                    .setCreditCardStatus(CreditCardStatus.UNEXPECTED_ERROR)
                    .setStatus(TransactionStatus.ERROR)
                    .setGatewayResponseCode("-2")
                    .setGatewayResponseMessage("Unsupported status.")
                    .setUpdatedAt(ZonedDateTime.now())
                    .setCreditCardUpdatedAt(ZonedDateTime.now())
                    .build();

        }

    }

    public static CreditCardTransaction defaultError(CreditCardTransaction t) {
        CreditCardTransaction.Builder builder = t.copy();

        return builder
                .setCreditCardStatus(CreditCardStatus.UNEXPECTED_ERROR)
                .setStatus(TransactionStatus.ERROR)
                .setGatewayResponseCode("-2")
                .setGatewayResponseMessage("Unsupported status.")
                .build();
    }

}
