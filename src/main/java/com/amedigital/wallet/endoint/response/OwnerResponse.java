package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.model.Owner;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class OwnerResponse {

    private String id;
    private String externalId;
    private String name;

    public OwnerResponse(Owner owner) {
        externalId = owner.getExternalId();
        name = owner.getName();
        owner.getUuid().ifPresent(uiid -> id = uiid);
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
                .append("name", name)
                .append("externalId", externalId)
                .build();
    }
}
