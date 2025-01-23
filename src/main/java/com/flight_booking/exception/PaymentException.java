package com.flight_booking.exception;

import com.flight_booking.exception.enums.ErrorStatus;

public class PaymentException extends RuntimeException {

    public PaymentException(ErrorStatus errorStatus) {
        super(String.valueOf(errorStatus));
    }
}