package com.amedigital.wallet.service.atom.response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class OrderPresenter {

    private String orderReference;
    private List<PaymentPresenter> payments;

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public List<PaymentPresenter> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentPresenter> payments) {
        this.payments = payments;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("orderReference", orderReference)
                .append("payments", payments)
                .build();
    }
}
