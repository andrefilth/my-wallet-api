package com.amedigital.wallet.endoint.converter.method;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.endoint.converter.RequestContext;
import com.amedigital.wallet.endoint.converter.order.primary.PrimaryRequestConverter;
import com.amedigital.wallet.endoint.request.method.*;
import com.amedigital.wallet.exceptions.AmeInternalException;
import com.amedigital.wallet.model.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

public class RouterMethodRequestConverter implements PrimaryRequestConverter<MethodRequest, Transaction> {

    private final CreditCardMethodRequestConverter creditCardConverter;
    private final CashMethodRequestConverter cashConverter;
    private final BankTransferMethodRequestConverter bankTransferConverter;
    private final CashBackMethodRequestConverter cashBackConverter;

    @Autowired
    public RouterMethodRequestConverter(CreditCardMethodRequestConverter creditCardConverter,
                                        CashMethodRequestConverter cashConverter,
                                        BankTransferMethodRequestConverter bankTransferConverter,
                                        CashBackMethodRequestConverter cashBackMethodRequestConverter) {

        this.creditCardConverter = creditCardConverter;
        this.cashConverter = cashConverter;
        this.bankTransferConverter = bankTransferConverter;
        this.cashBackConverter = cashBackMethodRequestConverter;
    }

    @Override
    public Transaction from(MethodRequest methodRequest, RequestContext context) {
        PaymentMethod type = methodRequest.getType();

        switch (type) {
            case CREDIT_CARD:
                return creditCardConverter.from((CreditCardMethodRequest) methodRequest, context);
            case CASH:
                return cashConverter.from((CashMethodRequest) methodRequest, context);
            case BANK_TRANSFER:
            	return bankTransferConverter.from((BankTransferMethodRequest) methodRequest, context);
            case CASH_BACK:
                return cashBackConverter.from((CashBackMethodRequest) methodRequest, context);
            default:
                throw new AmeInternalException("O tipo do método de pagamento não é suportada para conversão do request.");
        }
    }

}
