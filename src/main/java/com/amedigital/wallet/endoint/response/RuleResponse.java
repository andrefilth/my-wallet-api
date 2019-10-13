package com.amedigital.wallet.endoint.response;

import com.amedigital.wallet.constants.enuns.PaymentMethod;
import com.amedigital.wallet.constants.enuns.TakeRateUnit;

import java.time.temporal.ChronoUnit;

public class RuleResponse {

    private PaymentMethod paymentMethod;
    private Long takeRate;
    private TakeRateUnit takeRateUnit;
    private Long releaseTime;
    private ChronoUnit releaseTimeUnit;

    public RuleResponse() {
		super();
	}

    public RuleResponse(PaymentMethod paymentMethod, Long takeRate, TakeRateUnit takeRateUnit, Long releaseTime, ChronoUnit releaseTimeUnit) {
		super();
		this.paymentMethod = paymentMethod;
		this.takeRate = takeRate;
		this.takeRateUnit = takeRateUnit;
		this.releaseTime = releaseTime;
		this.releaseTimeUnit = releaseTimeUnit;
	}

	public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getTakeRate() {
        return takeRate;
    }

    public void setTakeRate(Long takeRate) {
        this.takeRate = takeRate;
    }

    public TakeRateUnit getTakeRateUnit() {
        return takeRateUnit;
    }

    public void setTakeRateUnit(TakeRateUnit takeRateUnit) {
        this.takeRateUnit = takeRateUnit;
    }

    public Long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public ChronoUnit getReleaseTimeUnit() {
        return releaseTimeUnit;
    }

    public void setReleaseTimeUnit(ChronoUnit releaseTimeUnit) {
        this.releaseTimeUnit = releaseTimeUnit;
    }



}