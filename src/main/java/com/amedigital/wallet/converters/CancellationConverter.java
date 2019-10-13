package com.amedigital.wallet.converters;

import com.amedigital.wallet.constants.enuns.CreditCardStatus;
import com.amedigital.wallet.constants.enuns.TransactionStatus;
import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.transaction.CreditCardTransaction;
import com.amedigital.wallet.service.atom.request.CancellationParameter;
import com.amedigital.wallet.service.atom.response.CancellationPresenter;
import com.amedigital.wallet.util.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

public class CancellationConverter {

    private static final Logger LOG = LoggerFactory.getLogger(CancellationConverter.class);

    public static CancellationParameter toCancellationParameter(final String cancellationReference, final Long totalAmountInCents) {
        LOG.info("Criando payload de cancelamento para o ATOM. cancellationReference: [{}], totalAmountInCents: [{}]",
                cancellationReference, totalAmountInCents);

        return new CancellationParameter(cancellationReference, totalAmountInCents);
    }

    public static CreditCardTransaction toCreditCardTransaction(CreditCardTransaction transaction, CancellationPresenter cancellationPresenter) {
        LOG.info("Convertendo a resposta de cancelamento do ATOM [{}]", cancellationPresenter);

        CreditCardTransaction.Builder builder = transaction.copy()
                .setCancelTid(cancellationPresenter.getTid())
                .setCancelNsu(cancellationPresenter.getNsu())
                .setGatewayCancellationReference(cancellationPresenter.getReference())
                .setGatewayResponseMessage(cancellationPresenter.getAcquirerResponseMessage())
                .setGatewayResponseCode(cancellationPresenter.getAcquirerResponseCode())
                .setCancelDate(cancellationPresenter.getCancelDate() != null ? MapperUtil.toZonedDateTime(cancellationPresenter.getCancelDate()) : null)
                .setUpdatedAt(ZonedDateTime.now());

        switch (cancellationPresenter.getState()) {
            case CANCELED:
                builder.setStatus(TransactionStatus.CANCELLED)
                        .setCreditCardStatus(CreditCardStatus.CANCELLED);
                break;
            case CANCELLATION_PENDING:
                builder.setStatus(TransactionStatus.PENDING)
                        .setCreditCardStatus(CreditCardStatus.CANCELLATION_PENDING);
                break;
            case CANCELLATION_REFUSED:
                builder.setStatus(TransactionStatus.ERROR)
                        .setCreditCardStatus(CreditCardStatus.CANCELLATION_REFUSED);
                break;
            case CANCELLATION_ERROR:
                builder.setStatus(TransactionStatus.ERROR)
                        .setCreditCardStatus(CreditCardStatus.CANCELLATION_ERROR);
                break;
            default:
                throw new AmeInvalidInputException("atom_error", "Não foi possível interpretar a resposta do gateway de pagamento.");
        }

        return builder.build();
    }
}
