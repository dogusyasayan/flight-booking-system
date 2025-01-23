package com.flight_booking.enums;

import lombok.Getter;

@Getter
public enum SeatStatus {

    AVAILABLE("Available"),
    SOLD("Sold"),
    RESERVED("Reserved");

    private String value;

    SeatStatus(String value) {
        this.value = value;
    }
}
