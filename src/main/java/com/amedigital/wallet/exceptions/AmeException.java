package com.amedigital.wallet.exceptions;

import com.amedigital.wallet.endoint.response.serializer.ExceptionSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

@JsonSerialize(using = ExceptionSerializer.class)
public class AmeException extends RuntimeException {

    private int httpStatus;
    private String errorCode;
    private Map<String, String> fields;

    public AmeException(int httpStatus, String errorCode, String description) {
        super(description);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public AmeException(int httpStatus, String errorCode, String description, Throwable cause) {
        super(description, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;

    }

    public AmeException(int httpStatus, String errorCode, String description, Map<String, String> fields, Throwable cause) {
        super(description, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.fields = fields;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("httpStatus", httpStatus)
                .append("errorCode", errorCode)
                .append("fields", fields)
                .build();
    }
}

