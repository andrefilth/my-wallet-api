package com.amedigital.wallet.model.order.secondary;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TakeRateUnit;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.temporal.ChronoUnit;

public class PaymentMethodRule {

    private PaymentMethod paymentMethod;
    private Long takeRate;
    private TakeRateUnit takeRateUnit;
    private Long releaseTime;
    private ChronoUnit releaseTimeUnit;


    private PaymentMethodRule(Builder builder) {
        paymentMethod = builder.paymentMethod;
        takeRate = builder.takeRate;
        releaseTime = builder.releaseTime;
        releaseTimeUnit = builder.releaseTimeUnit;
        takeRateUnit = builder.takeRateUnit;
    }

    public PaymentMethodRule.Builder copy() {
        return new Builder()
                .setPaymentMethod(paymentMethod)
                .setTakeRate(takeRate)
                .setReleaseTime(releaseTime)
                .setReleaseTimeUnit(releaseTimeUnit)
                .setTakeRateUnit(takeRateUnit);
    }


    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Long getTakeRate() {
        return takeRate;
    }

    public Long getReleaseTime() {
        return releaseTime != null ? releaseTime : 0;
    }

    public ChronoUnit getReleaseTimeUnit() {
        return releaseTimeUnit != null ? releaseTimeUnit : ChronoUnit.DAYS;
    }

    public TakeRateUnit getTakeRateUnit() {
        return takeRateUnit != null ? takeRateUnit : TakeRateUnit.PERCENT;
    }

    public static class Builder{

        private PaymentMethod paymentMethod;
        private Long takeRate;
        private Long releaseTime;
        private ChronoUnit releaseTimeUnit;
        private TakeRateUnit takeRateUnit;


        public PaymentMethodRule.Builder setPaymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public PaymentMethodRule.Builder setTakeRate(Long takeRate) {
            this.takeRate = takeRate;
            return this;
        }

        public PaymentMethodRule.Builder setTakeRateUnit(TakeRateUnit takeRateUnit) {
            this.takeRateUnit = takeRateUnit;
            return this;
        }

        public PaymentMethodRule.Builder setReleaseTime(Long releaseTime) {
            this.releaseTime = releaseTime;
            return this;
        }


        public PaymentMethodRule.Builder setReleaseTimeUnit(ChronoUnit releaseTimeUnit) {
            this.releaseTimeUnit = releaseTimeUnit;
            return this;
        }



        public PaymentMethodRule build() {
            return new PaymentMethodRule(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("paymentMethod", paymentMethod)
                .append("takeRate", takeRate)
                .append("takeRateUnit", takeRateUnit)
                .append("releaseTime", releaseTime)
                .append("releaseTimeUnit", releaseTimeUnit)
                .build();
    }

}
