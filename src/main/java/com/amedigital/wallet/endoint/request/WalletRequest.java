package com.amedigital.wallet.endoint.request;

import com.amedigital.wallet.constants.enuns.WalletType;
import com.amedigital.wallet.model.Wallet;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class WalletRequest {

    private String name;
    private WalletType type;
    private boolean main = true;
    private OwnerRequest owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WalletType getType() {
        return type;
    }

    public void setType(WalletType type) {
        this.type = type;
    }

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public OwnerRequest getOwner() {
        return owner;
    }

    public void setOwner(OwnerRequest owner) {
        this.owner = owner;
    }

    public Wallet toModel() {
        return Wallet.builder()
                .setName(name)
                .setType(type)
                .setMain(main)
                .setOwner(owner == null ? null : owner.toModel())
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("name", name)
                .append("type", type)
                .append("main", main)
                .append("owner", owner)
                .build();
    }

}
