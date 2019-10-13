package com.amedigital.wallet.model.order;

import com.amedigital.wallet.constants.enuns.ActionType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.ZonedDateTime;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class Action {

    private final Long id;
    private final Long parentId;
    private final ActionType type;
    private final ZonedDateTime createdAt;

    private Action(Builder builder) {
        this.id = builder.id;
        this.parentId = builder.parentId;
        this.type = builder.type;
        this.createdAt = builder.createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public ActionType getType() {
        return type;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Builder copy() {
        return new Builder()
                .setId(id)
                .setParentId(parentId)
                .setType(type)
                .setCreatedAt(createdAt);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("id", id)
                .append("parentId", parentId)
                .append("type", type)
                .append("createdAt", createdAt)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long parentId;
        private ActionType type;
        private ZonedDateTime createdAt;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setParentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder setType(ActionType type) {
            this.type = type;
            return this;
        }

        public Builder setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Action build() {
            return new Action(this);
        }
    }
}
