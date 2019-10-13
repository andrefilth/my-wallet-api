package com.amedigital.wallet.util;

import com.amedigital.wallet.constants.enuns.TakeRateUnit;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeeManager {

    private final Long initialAmount;
    private final TakeRateUnit takeRateUnit;
    private final Long takeRate;

    public FeeManager(Long initialAmount, Long takeRate, TakeRateUnit takeRateUnit) {
        this.initialAmount = initialAmount;
        this.takeRate = takeRate;
        this.takeRateUnit = takeRateUnit;
    }

    public boolean isFree() {
        return BigDecimal.ZERO.equals(takeRate);
    }

    public Long calcTransactionAmount() {
        if(isFree()) {
            return initialAmount;
        } else {
            if(TakeRateUnit.PERCENT.equals(takeRateUnit)) {
                Long takeRateAmountInCents = MoneyUtil.calculateTakeRateAmountInCents(initialAmount, takeRate, takeRateUnit);
                return initialAmount - takeRateAmountInCents;
            } else {
                return initialAmount - takeRate;
            }
        }
    }

    public List<AmountParcel> calcInstallmentsAmount(Integer installments) {

//        var amountInCents = calcTransactionAmount();
        var amountInCents = initialAmount;

        var installmentsAmount = amountInCents / installments;

        var installmentsRemainder = amountInCents % installments;

        return Stream.iterate(1, i -> i + 1)
                .limit(installments)
                .map(i -> {
                    if(i.equals(1)) {
                        Long grossAmountInCents = installmentsAmount;
                        Long takeRateAmountInCents = MoneyUtil.calculateTakeRateAmountInCents(grossAmountInCents, takeRate, takeRateUnit);
                        long netAmountInCentsParcel = (installmentsAmount + installmentsRemainder) - takeRateAmountInCents;

                        return new AmountParcel(netAmountInCentsParcel, takeRateAmountInCents, grossAmountInCents, i);
                    } else {
                        Long grossAmountInCents = installmentsAmount;
                        Long takeRateAmountInCents = MoneyUtil.calculateTakeRateAmountInCents(grossAmountInCents, takeRate, takeRateUnit);
                        long netAmountInCentsParcel = installmentsAmount - takeRateAmountInCents;

                        return new AmountParcel(netAmountInCentsParcel, takeRateAmountInCents, grossAmountInCents, i);
                    }})
                .collect(Collectors.toList());
    }

    public AmountParcel getAmountParcel() {
        return calcInstallmentsAmount(1).get(0);
    }

    public class AmountParcel {

        /**
         * Valor da parcela liquida (já descontado a taxa) = netAmountInCents
         */
        private final Long amount;
        /**
         * Valor em centavos da taxa que está sendo aplicada
         */
        private final Long takeRateAmountInCents;

        /**
         * Valor bruto do valor aplicado (amount + takeRateAmountInCents)
         */
        private final Long grossAmountInCents;

        private final Integer parcelNumber;



        public AmountParcel(Long amount, Long takeRateAmountInCents, Long grossAmountInCents, Integer parcelNumber) {
            this.amount = amount;
            this.takeRateAmountInCents = takeRateAmountInCents;
            this.grossAmountInCents = grossAmountInCents;
            this.parcelNumber = parcelNumber;
        }

        public Long getAmount() {
            return amount;
        }

        public Long getTakeRateAmountInCents() {
            return takeRateAmountInCents;
        }

        public Integer getParcelNumber() {
            return parcelNumber;
        }

        public Long getGrossAmountInCents() {
            return grossAmountInCents;
        }
    }

}