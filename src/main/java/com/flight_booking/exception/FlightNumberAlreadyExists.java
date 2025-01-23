package com.flight_booking.exception;

import com.flight_booking.exception.enums.ErrorStatus;

public class FlightNumberAlreadyExists extends RuntimeException {

    public FlightNumberAlreadyExists(ErrorStatus errorStatus) {
        super(String.valueOf(errorStatus));
    }
}