package com.flight_booking.exception;

import com.flight_booking.exception.enums.ErrorStatus;

public class FlightNotFoundException extends RuntimeException {

    public FlightNotFoundException(ErrorStatus errorStatus) {
        super(String.valueOf(errorStatus));
    }
}