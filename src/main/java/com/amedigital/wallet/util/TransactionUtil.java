package com.amedigital.wallet.util;

import org.apache.commons.lang3.RandomUtils;

import java.time.LocalTime;

public class TransactionUtil {

    public static String createNsu() {
        var timePart = String.valueOf(LocalTime.now().toNanoOfDay()).substring(0, 11);

        var randomPart = String.valueOf(RandomUtils.nextLong(0L, 9L));

        return randomPart + timePart;
    }
}
