package com.flight_booking.enums;

import lombok.Getter;

@Getter
public enum FlightStatus {
    UPCOMING("Upcoming"),
    CANCELLED("Cancelled"),
    DELAYED("Delayed"),
    COMPLETED("Completed");

    private final String value;

    FlightStatus(String value) {
        this.value = value;
    }
}


