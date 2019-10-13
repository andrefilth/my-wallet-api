package com.amedigital.wallet.endoint.response.legacy;

import com.amedigital.wallet.constants.enuns.WalletType;
import com.amedigital.wallet.endoint.response.BalanceResponse;
import com.amedigital.wallet.endoint.response.WalletBalanceResponse;
import com.amedigital.wallet.model.Wallet;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WResponse {

    private final String id;
    private final String name;
    private final WalletType type;
    private final OResponse owner;
    private final BalanceResponse balance;

    public WResponse(Wallet wallet) {
        this.id = wallet.getUuid().get();
        this.name = wallet.getName();
        this.type = wallet.getType();
        this.owner = new OResponse(wallet.getOwner());
        this.balance = new BalanceResponse(wallet.getBalance().getCashBalance());
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

    public BalanceResponse getBalance() {
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
