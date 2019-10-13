package com.amedigital.wallet.endoint.request;

import com.amedigital.wallet.constants.enuns.DocumentType;
import com.amedigital.wallet.model.Owner;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class OwnerRequest {

    private String name;
    private String externalId;
    private String email;
    private String document;
    private DocumentType documentType;

    public OwnerRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Owner toModel() {
        return Owner.builder()
                .setExternalId(externalId)
                .setName(name)
                .setEmail(email)
                .setDocument(document)
                .setDocumentType(documentType)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("name", name)
                .append("externalId", externalId)
                .append("email", email)
                .append("document", document)
                .append("documentType", documentType)
                .build();
    }

}

