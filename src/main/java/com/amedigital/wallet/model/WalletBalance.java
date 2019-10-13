package com.amedigital.wallet.model;

public class WalletBalance {

    private final Balance cashBalance;
    private final Balance cashBackBalance;

    public WalletBalance(Balance cashBalance, Balance cashBackBalance) {
        this.cashBalance = cashBalance;
        this.cashBackBalance = cashBackBalance;
    }

    public Balance getCashBalance() {
        return cashBalance;
    }

    public Balance getCashBackBalance() {
        return cashBackBalance;
    }

}
