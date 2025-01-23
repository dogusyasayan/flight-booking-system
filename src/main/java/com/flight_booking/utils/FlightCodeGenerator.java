package com.flight_booking.utils;

import java.util.concurrent.ThreadLocalRandom;

public class FlightCodeGenerator {

    private static final String PREFIX = "TK";
    private static final int RANDOM_DIGIT_COUNT = 5;

    public static String generateFlightCode() {
        StringBuilder sb = new StringBuilder(PREFIX);

        for (int i = 0; i < RANDOM_DIGIT_COUNT; i++) {
            int digit = ThreadLocalRandom.current().nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

}
