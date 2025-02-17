package com.flight_booking.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorStatus {

    FLIGHT_NOT_FOUND("EX001", HttpStatus.NOT_FOUND),
    FLIGHT_ALREADY_EXISTS("EX002", HttpStatus.CONFLICT),
    SEAT_NOT_FOUND("EX003", HttpStatus.NOT_FOUND),
    INVALID_DATE_TIME_FORMAT("EX004", HttpStatus.BAD_REQUEST),
    SEAT_ALREADY_SOLD("EX005", HttpStatus.CONFLICT),
    PAYMENT_FAILED("EX006", HttpStatus.BAD_REQUEST),
    FLIGHT_ALREADY_DEPARTED("EX007", HttpStatus.BAD_REQUEST);

    private final String code;
    private final HttpStatus httpStatus;
}
