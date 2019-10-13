package com.amedigital.wallet.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class MapperUtil {

    private static ZoneId zoneId = ZoneOffset.UTC;

    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return ZonedDateTime.of(localDateTime, zoneId);

        }

        return null;
    }

}
