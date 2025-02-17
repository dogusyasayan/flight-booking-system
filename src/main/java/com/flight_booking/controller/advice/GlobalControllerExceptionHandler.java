package com.flight_booking.controller.advice;

import com.flight_booking.exception.FlightAlreadyDepartedException;
import com.flight_booking.exception.FlightNotFoundException;
import com.flight_booking.exception.FlightNumberAlreadyExists;
import com.flight_booking.exception.InvalidDateTimeFormatException;
import com.flight_booking.exception.PaymentException;
import com.flight_booking.exception.SeatAlreadySold;
import com.flight_booking.exception.SeatNotFoundException;
import com.flight_booking.exception.enums.ErrorStatus;
import com.flight_booking.model.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(FlightNotFoundException exception) {
        return instanceError(exception, ErrorStatus.FLIGHT_NOT_FOUND);
    }

    @ExceptionHandler(FlightNumberAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handle(FlightNumberAlreadyExists exception) {
        return instanceError(exception, ErrorStatus.FLIGHT_ALREADY_EXISTS);
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(SeatNotFoundException exception) {
        return instanceError(exception, ErrorStatus.SEAT_NOT_FOUND);
    }

    @ExceptionHandler(SeatAlreadySold.class)
    public ResponseEntity<ErrorResponse> handle(SeatAlreadySold exception) {
        return instanceError(exception, ErrorStatus.SEAT_ALREADY_SOLD);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handle(PaymentException exception) {
        return instanceError(exception, ErrorStatus.PAYMENT_FAILED);
    }

    @ExceptionHandler(FlightAlreadyDepartedException.class)
    public ResponseEntity<ErrorResponse> handle(FlightAlreadyDepartedException exception) {
        return instanceError(exception, ErrorStatus.FLIGHT_ALREADY_DEPARTED);
    }

    @ExceptionHandler(InvalidDateTimeFormatException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidDateTimeFormatException exception) {
        return instanceError(exception, ErrorStatus.INVALID_DATE_TIME_FORMAT);
    }

    private ResponseEntity<ErrorResponse> instanceError(RuntimeException ex, ErrorStatus errorStatus) {
        log.error("{} exception occurred.", errorStatus.getCode(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(ex.getClass().getSimpleName())
                .errors(Collections.singletonList(errorStatus.getCode()))
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponse, errorStatus.getHttpStatus());
    }
}
