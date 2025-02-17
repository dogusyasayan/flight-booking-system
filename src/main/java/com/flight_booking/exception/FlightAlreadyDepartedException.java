package com.flight_booking.exception;

import com.flight_booking.exception.enums.ErrorStatus;

public class FlightAlreadyDepartedException extends RuntimeException {

    public FlightAlreadyDepartedException(ErrorStatus errorStatus) {
        super(String.valueOf(errorStatus));
    }
}