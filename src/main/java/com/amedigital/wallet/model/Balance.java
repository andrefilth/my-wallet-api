package com.amedigital.wallet.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Balance {

    private final Long available;
    private final Long futureCredit;
    private final Long futureDebit;

    private Balance(Builder builder) {
        this.available = builder.available;
        this.futureCredit = builder.futureCredit;
        this.futureDebit = builder.futureDebit;
    }

    public Long getAvailable() {
        return available;
    }

    public Long getFutureCredit() {
        return futureCredit;
    }

    public Long getFutureDebit() {
        return futureDebit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Balance emptyBalance() {
        return Balance.builder().build();
    }

    public static class Builder {

        private Long available = 0L;
        private Long futureCredit = 0L;
        private Long futureDebit = 0L;

        public Builder setAvailable(Long available) {
            this.available = available;
            return this;
        }

        public Builder setFutureCredit(Long futureCredit) {
            this.futureCredit = futureCredit;
            return this;
        }

        public Builder setFutureDebit(Long futureDebit) {
            this.futureDebit = futureDebit;
            return this;
        }

        public Balance build() {
            return new Balance(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("available", available)
                .append("futureCredit", futureCredit)
                .append("futureDebit", futureDebit)
                .build();
    }
}
