package com.amedigital.wallet.endoint.response.legacy;

import com.amedigital.wallet.model.Owner;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class OResponse {

    private final String id;
    private final String externalId;
    private final String name;

    public OResponse(Owner owner) {
        this.id = owner.getUuid().get();
        this.externalId = owner.getExternalId();
        this.name = owner.getName();
    }

    public String getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("externalId", externalId)
                .append("name", name)
                .build();
    }
}
