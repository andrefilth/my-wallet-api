package com.amedigital.wallet.endoint.request.order;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TakeRateUnit;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class PaymentMethodRuleRequest {

    private PaymentMethod paymentMethod;
    private Long takeRate;
    private TakeRateUnit takeRateUnit;
    private Long releaseTime;
    private ChronoUnit releaseTimeUnit;


    public PaymentMethodRuleRequest() {
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Long getTakeRate() {
        return takeRate;
    }

    public Long getReleaseTime() {
        return releaseTime;
    }

    public ChronoUnit getReleaseTimeUnit() {
        return releaseTimeUnit;
    }

    public TakeRateUnit getTakeRateUnit() {
        return takeRateUnit;
    }

    public void setTakeRateUnit(TakeRateUnit takeRateUnit) {
        this.takeRateUnit = takeRateUnit;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentMethodRuleRequest that = (PaymentMethodRuleRequest) o;
        return paymentMethod == that.paymentMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentMethod);
    }
}