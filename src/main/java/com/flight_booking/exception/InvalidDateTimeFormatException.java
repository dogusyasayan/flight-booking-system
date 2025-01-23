package com.flight_booking.exception;

import com.flight_booking.exception.enums.ErrorStatus;

public class InvalidDateTimeFormatException extends RuntimeException {

    public InvalidDateTimeFormatException(ErrorStatus errorStatus) {
        super(String.valueOf(errorStatus));
    }
}
