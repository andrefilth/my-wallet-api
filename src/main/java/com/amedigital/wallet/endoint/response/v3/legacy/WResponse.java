package com.amedigital.wallet.endoint.response.v3.legacy;

import com.amedigital.wallet.constants.enuns.WalletType;
import com.amedigital.wallet.endoint.response.WalletBalanceResponse;
import com.amedigital.wallet.endoint.response.legacy.OResponse;
import com.amedigital.wallet.model.Wallet;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WResponse {

    private final String id;
    private final String name;
    private final WalletType type;
    private final OResponse owner;
    private final WalletBalanceResponse balance;

    public WResponse(Wallet wallet) {
        this.id = wallet.getUuid().get();
        this.name = wallet.getName();
        this.type = wallet.getType();
        this.owner = new OResponse(wallet.getOwner());
        this.balance = new WalletBalanceResponse(wallet.getBalance().getCashBalance(), wallet.getBalance().getCashBackBalance());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public WalletType getType() {
        return type;
    }

    public OResponse getOwner() {
        return owner;
    }

    public WalletBalanceResponse getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("name", name)
                .append("type", type)
                .append("owner", owner)
                .append("balance", balance)
                .build();
    }
}
