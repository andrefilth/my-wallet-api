package com.amedigital.wallet.model;

import com.amedigital.wallet.constants.enuns.DocumentType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;
import java.util.Optional;

public class Owner {

    private final Long id;
    private final String uuid;
    private final String externalId;
    private final String name;
    private final String email;
    private final String document;
    private final DocumentType documentType;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    private Owner(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.externalId = builder.externalId;
        this.name = builder.name;
        this.email = builder.email;
        this.document = builder.document;
        this.documentType = builder.documentType;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getUuid() {
        return Optional.ofNullable(uuid);
    }

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDocument() {
        return document;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public Optional<ZonedDateTime> getCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public Optional<ZonedDateTime> getUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public Builder copy() {
        return new Builder()
                .setCreatedAt(createdAt)
                .setDocument(document)
                .setDocumentType(documentType)
                .setEmail(email)
                .setExternalId(externalId)
                .setId(id)
                .setName(name)
                .setUpdatedAt(updatedAt)
                .setUuid(uuid);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("uuid", uuid)
                .append("name", name)
                .append("externalId", externalId)
                .append("email", email)
                .append("document", document)
                .append("documentType", documentType)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .build();
    }

    public static final class Builder {
        private Long id;
        private String uuid;
        private String externalId;
        private String name;
        private String email;
        private String document;
        private DocumentType documentType;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;


        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setDocument(String document) {
            this.document = document;
            return this;
        }

        public Builder setDocumentType(DocumentType documentType) {
            this.documentType = documentType;
            return this;
        }

        public Builder setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Owner build() {
            return new Owner(this);
        }

    }

}
