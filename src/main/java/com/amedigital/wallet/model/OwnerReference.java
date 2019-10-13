package com.amedigital.wallet.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class OwnerReference {

    private final String name;

    private OwnerReference(Builder builder) {
        this.name = builder.name;
    }

    public String getName() {
        return name;
    }

    public static final class Builder {
        private String name;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public OwnerReference build() {
            return new OwnerReference(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("name", name)
                .build();
    }
}
