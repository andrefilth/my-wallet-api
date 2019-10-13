package com.amedigital.wallet.service.atom.response.enums;

public enum CancellationState {

    CREATED("The cancellation was created for processing with success."),
    CANCELLATION_PENDING("Cancellation was sent and it is waiting for processing."),
    CANCELED("Cancellation was completed."),
    CANCELLATION_REFUSED("Cancellation was refused."),
    CANCELLATION_ERROR("Occurred an error to process the cancellation.");

    private final String description;

    CancellationState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
