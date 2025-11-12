package com.oxam.klume.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parseToLocalDateTime(final String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }

    public static LocalDate parseToLocalDate(final String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    public static String format(final LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String format(final LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static LocalDateTime parseAuto(final String str) {
        if (str.length() == 10) {
            return LocalDate.parse(str, DATE_FORMATTER).atStartOfDay();
        } else {
            return LocalDateTime.parse(str, DATE_TIME_FORMATTER);
        }
    }
}