package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.model.Balance;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WalletBalanceResponse {

    private final Balance cashBalance;
    private final Balance cashBackBalance;

    public WalletBalanceResponse(Balance cashBalance, Balance cashBackBalance) {
        this.cashBalance = cashBalance;
        this.cashBackBalance = cashBackBalance;
    }

    public Balance getCashBalance() {
        return cashBalance;
    }

    public Balance getCashBackBalance() {
        return cashBackBalance;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("cashBalance", cashBalance)
                .append("cashBackBalance", cashBackBalance)
                .toString();
    }
}
