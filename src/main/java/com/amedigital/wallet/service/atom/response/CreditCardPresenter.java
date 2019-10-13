package com.amedigital.wallet.service.atom.response;

import com.amedigital.wallet.service.atom.response.enums.CardBrand;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CreditCardPresenter {

    private String holderName;
    private String maskedNumber;
    private CardBrand brand;
    private Integer expirationMonth;
    private Integer expirationYear;


    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public CardBrand getBrand() {
        return brand;
    }

    public void setBrand(CardBrand brand) {
        this.brand = brand;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("holderName", holderName)
                .append("maskedNumber", maskedNumber)
                .append("brand", brand)
                .append("expirationMonth", expirationMonth)
                .append("expirationYear", expirationYear)
                .build();
    }
}
