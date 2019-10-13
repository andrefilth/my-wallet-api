package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.model.Balance;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BalanceResponse {

    private final Long available;
    private final Long futureCredit;
    private final Long futureDebit;

    public BalanceResponse(Balance balance) {
        available = balance.getAvailable();
        futureCredit = balance.getFutureCredit();
        futureDebit = balance.getFutureDebit();
    }

    public Long getAvailable() {
        return available;
    }

    public Long getFutureCredit() {
        return futureCredit;
    }

    public Long getFutureDebit() {
        return futureDebit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("available", available)
                .append("futureCredit", futureCredit)
                .append("futureDebit", futureDebit)
                .build();
    }
}