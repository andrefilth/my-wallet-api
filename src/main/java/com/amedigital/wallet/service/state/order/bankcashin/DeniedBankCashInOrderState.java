package com.amedigital.wallet.service.state.order.bankcashin;

import static com.amedigital.wallet.constants.Constants.BANK_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.deniedMessageException;

import org.springframework.stereotype.Service;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.BankCashInOrder;
import com.amedigital.wallet.service.state.OrderState;

import reactor.core.publisher.Mono;

@Service
public class DeniedBankCashInOrderState implements OrderState<BankCashInOrder> {

    @Override
    public Mono<BankCashInOrder> create(BankCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("criar", BANK_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<BankCashInOrder> authorize(BankCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("autorizar", BANK_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<BankCashInOrder> capture(BankCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("capturar", BANK_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<BankCashInOrder> cancel(BankCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                deniedMessageException("cancelar", BANK_CASH_IN_ORDER_TYPE));
    }
}
