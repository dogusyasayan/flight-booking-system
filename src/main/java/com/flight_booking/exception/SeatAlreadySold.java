package com.flight_booking.exception;

import com.flight_booking.exception.enums.ErrorStatus;

public class SeatAlreadySold extends RuntimeException {

    public SeatAlreadySold(ErrorStatus errorStatus) {
        super(String.valueOf(errorStatus));
    }
}