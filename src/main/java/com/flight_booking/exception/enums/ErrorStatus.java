package com.flight_booking.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorStatus {

    FLIGHT_NOT_FOUND("EX001", HttpStatus.NOT_FOUND),
    FLIGHT_ALREADY_EXITS("EX002", HttpStatus.NOT_FOUND),
    AVAILABLE_SEAT_NOT_FOUND("EX003", HttpStatus.NOT_FOUND),
    SEAT_NOT_FOUND("EX004", HttpStatus.NOT_FOUND),
    INVALID_DATE_TIME_FORMAT("EX005", HttpStatus.NOT_FOUND),
    SEAT_ALREADY_SOLD("EX006", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED("EX007", HttpStatus.NOT_FOUND);

    private final String code;
    private final HttpStatus httpStatus;
}
