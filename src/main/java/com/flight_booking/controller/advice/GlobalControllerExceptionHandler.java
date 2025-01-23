package com.flight_booking.controller.advice;

import com.flight_booking.exception.FlightNotFoundException;
import com.flight_booking.exception.FlightNumberAlreadyExists;
import com.flight_booking.exception.InvalidDateTimeFormatException;
import com.flight_booking.exception.PaymentException;
import com.flight_booking.exception.SeatAlreadySold;
import com.flight_booking.exception.SeatNotFoundException;
import com.flight_booking.model.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        return instanceError(exception, "cart-not-found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FlightNumberAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handle(FlightNumberAlreadyExists exception) {
        return instanceError(exception, "product-not-found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(SeatNotFoundException exception) {
        return instanceError(exception, "seat-not-found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SeatAlreadySold.class)
    public ResponseEntity<ErrorResponse> handle(SeatAlreadySold exception) {
        return instanceError(exception, "seat-already-sold", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handle(PaymentException exception) {
        return instanceError(exception, "payment-exception", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidDateTimeFormatException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidDateTimeFormatException exception) {
        return instanceError(exception, "invalid-date-time-format-exception", HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorResponse> instanceError(RuntimeException ex, String errorMessage, HttpStatus status) {
        log.error(errorMessage + " exception occurred.", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(ex.getClass().getSimpleName())
                .errors(Collections.singletonList(ex.getMessage()))
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}

