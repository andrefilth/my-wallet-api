package com.amedigital.wallet.endoint.response.v3;

import com.amedigital.wallet.constants.enuns.WalletType;
import com.amedigital.wallet.endoint.response.BalanceResponse;
import com.amedigital.wallet.endoint.response.OwnerResponse;
import com.amedigital.wallet.endoint.response.WalletBalanceResponse;
import com.amedigital.wallet.model.Wallet;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;

public class WalletResponse {

    private final String id;
    private final String name;
    private final WalletType type;
    private final boolean main;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final OwnerResponse owner;
    private final WalletBalanceResponse balance;

    public WalletResponse(Wallet wallet) {
        name = wallet.getName();
        type = wallet.getType();
        main = wallet.isMain();
        owner = new OwnerResponse(wallet.getOwner());
        id = wallet.getUuid().get();
        createdAt = wallet.getCreatedAt().isEmpty()? null : wallet.getCreatedAt().get();
        updatedAt = wallet.getUpdatedAt().isEmpty()? null : wallet.getUpdatedAt().get();
        balance = new WalletBalanceResponse(wallet.getBalance().getCashBalance(), wallet.getBalance().getCashBackBalance());
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

    public boolean isMain() {
        return main;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OwnerResponse getOwner() {
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
                .append("main", main)
                .append("owner", owner)
                .append("balance", balance)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .build();
    }
}
