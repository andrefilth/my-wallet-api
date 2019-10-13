package com.amedigital.wallet.util;

import com.amedigital.wallet.constants.enuns.TakeRateUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtil {

    private static final BigDecimal CEM = new BigDecimal("100");

    public static Long toCents(BigDecimal amount) {
        BigDecimal cents = new BigDecimal(100);
        return cents.multiply(amount).longValue();
    }

    public static BigDecimal toAmount(Long cents) {
        BigDecimal c = new BigDecimal(cents);

        return c.divide(new BigDecimal(100), RoundingMode.HALF_UP);
    }

    public static BigDecimal toPercent(Long numerador, Long denominador) {
        BigDecimal aNumerador = new BigDecimal(numerador);

        BigDecimal aDemoninador = new BigDecimal(denominador);

        return aNumerador.multiply(new BigDecimal(100)).divide(aDemoninador, RoundingMode.HALF_UP);
    }


    public static Long calculateTakeRateAmountInCents(Long amountInCents, Long takeRate, TakeRateUnit takeRateUnit) {
        if(takeRate != null && amountInCents != null && takeRate > 0 && amountInCents > 0 && takeRateUnit != null) {
            switch (takeRateUnit) {
                case PERCENT:
                    BigDecimal takeRateInPercentValue = new BigDecimal(takeRate.toString()).divide(CEM, RoundingMode.HALF_UP).divide(CEM);
                    return takeRateInPercentValue.multiply(new BigDecimal(amountInCents)).longValue();
                case CURRENCY:
                    return takeRate;
                default: return 0L;
            }
        }

        return 0L;
    }

}
