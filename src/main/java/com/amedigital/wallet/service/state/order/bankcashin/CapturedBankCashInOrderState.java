package com.amedigital.wallet.service.state.order.bankcashin;

import static com.amedigital.wallet.constants.Constants.BANK_CASH_IN_ORDER_TYPE;
import static com.amedigital.wallet.constants.Messages.capturedMessageException;

import org.springframework.stereotype.Service;

import com.amedigital.wallet.exceptions.AmeInvalidInputException;
import com.amedigital.wallet.model.order.primary.BankCashInOrder;
import com.amedigital.wallet.service.state.OrderState;

import reactor.core.publisher.Mono;

@Service
public class CapturedBankCashInOrderState implements OrderState<BankCashInOrder> {

    @Override
    public Mono<BankCashInOrder> create(BankCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("criar", BANK_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<BankCashInOrder> authorize(BankCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("autorizar", BANK_CASH_IN_ORDER_TYPE));
    }

    @Override
    public Mono<BankCashInOrder> capture(BankCashInOrder order) {
        return Mono.just(order);
    }

    @Override
    public Mono<BankCashInOrder> cancel(BankCashInOrder order) {
        throw new AmeInvalidInputException("wallet_validation",
                capturedMessageException("cancelar", BANK_CASH_IN_ORDER_TYPE));
    }
}
