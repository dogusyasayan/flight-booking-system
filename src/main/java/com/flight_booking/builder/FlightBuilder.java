package com.flight_booking.builder;

import com.flight_booking.domain.Flight;
import com.flight_booking.enums.FlightStatus;
import com.flight_booking.exception.InvalidDateTimeFormatException;
import com.flight_booking.exception.enums.ErrorStatus;
import com.flight_booking.model.request.CreateFlightRequest;
import com.flight_booking.utils.DateTimeUtils;
import com.flight_booking.utils.FlightCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class FlightBuilder {

    public Flight buildFlight(CreateFlightRequest createFlightRequest) {
        if (!isValidDateTimeFormat(createFlightRequest.getDepartureTime()) ||
                !isValidDateTimeFormat(createFlightRequest.getArrivalTime())) {
            throw new InvalidDateTimeFormatException(ErrorStatus.INVALID_DATE_TIME_FORMAT);
        }

        return Flight.builder()
                .flightCode(FlightCodeGenerator.generateFlightCode())
                .name(createFlightRequest.getName())
                .description(createFlightRequest.getDescription())
                .flightStatus(FlightStatus.UPCOMING)
                .departureTime(DateTimeUtils.parseToLocalDateTime(createFlightRequest.getDepartureTime()))
                .arrivalTime(DateTimeUtils.parseToLocalDateTime(createFlightRequest.getArrivalTime()))
                .build();
    }

    private boolean isValidDateTimeFormat(String dateTimeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime.parse(dateTimeString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}