package com.flight_booking.enums;

import lombok.Getter;

@Getter
public enum SeatStatus {

    AVAILABLE("Available"),
    SOLD("Sold");

    private String value;

    SeatStatus(String value) {
        this.value = value;
    }
}
