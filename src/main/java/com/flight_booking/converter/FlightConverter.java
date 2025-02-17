package com.flight_booking.converter;

import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.model.request.UpdateFlightRequest;
import com.flight_booking.model.response.flight.FlightInformationResponse;
import com.flight_booking.model.response.flight.FlightResponse;
import com.flight_booking.model.response.seat.SeatInformationResponse;
import com.flight_booking.utils.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FlightConverter {

    public FlightResponse apply(Flight flight) {
        return FlightResponse.builder()
                .flightCode(flight.getFlightCode())
                .name(flight.getName())
                .description(flight.getDescription())
                .formattedDepartureTime(DateTimeUtils.formatLocalDateTime(flight.getDepartureTime()))
                .formattedArrivalTime(DateTimeUtils.formatLocalDateTime(flight.getArrivalTime()))
                .pastFlight(flight.getDepartureTime().isBefore(LocalDateTime.now()))
                .build();
    }

    public Flight updateFlightInfo(Flight flight, UpdateFlightRequest updateFlightRequest) {
        Optional.ofNullable(updateFlightRequest.getName()).ifPresent(flight::setName);
        Optional.ofNullable(updateFlightRequest.getDescription()).ifPresent(flight::setDescription);
        Optional.ofNullable(updateFlightRequest.getFlightStatus()).ifPresent(flight::setFlightStatus);
        Optional.ofNullable(updateFlightRequest.getDepartureTime())
                .map(DateTimeUtils::parseToLocalDateTime)
                .ifPresent(flight::setDepartureTime);
        Optional.ofNullable(updateFlightRequest.getArrivalTime())
                .map(DateTimeUtils::parseToLocalDateTime)
                .ifPresent(flight::setArrivalTime);
        return flight;
    }

    public FlightInformationResponse infoFlight(Flight flight, Set<Seat> seats) {
        FlightInformationResponse response = new FlightInformationResponse();
        response.setFlightCode(flight.getFlightCode());
        response.setName(flight.getName());
        response.setDescription(flight.getDescription());
        response.setInformationResponses(getSeatStatusInfo(seats));
        return response;
    }

    public List<SeatInformationResponse> getSeatStatusInfo(Set<Seat> seats) {
        return seats.stream()
                .sorted(Comparator.comparing(Seat::getSeatNumber))
                .map(seat -> new SeatInformationResponse(seat.getSeatNumber(), seat.getSeatStatus().getValue(), seat.getSeatPrice()))
                .collect(Collectors.toList());
    }
}