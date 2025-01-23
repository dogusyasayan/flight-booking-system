package com.flight_booking.exception;

import com.flight_booking.exception.enums.ErrorStatus;

public class SeatNotFoundException extends RuntimeException {

    public SeatNotFoundException(ErrorStatus errorStatus) {
        super(String.valueOf(errorStatus));
    }
}