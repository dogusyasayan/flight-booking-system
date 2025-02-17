package com.flight_booking.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter PARSE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static LocalDateTime parseToLocalDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, PARSE_FORMATTER);
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }
}
