package com.flight_booking.service;

import com.flight_booking.builder.FlightBuilder;
import com.flight_booking.converter.FlightConverter;
import com.flight_booking.domain.Flight;
import com.flight_booking.exception.FlightNotFoundException;
import com.flight_booking.exception.FlightNumberAlreadyExists;
import com.flight_booking.exception.enums.ErrorStatus;
import com.flight_booking.model.request.CreateFlightRequest;
import com.flight_booking.model.request.UpdateFlightRequest;
import com.flight_booking.model.response.flight.FlightInformationResponse;
import com.flight_booking.model.response.flight.FlightResponse;
import com.flight_booking.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightBuilder flightBuilder;
    private final FlightConverter flightConverter;

    @Transactional
    public FlightResponse createFlight(CreateFlightRequest createFlightRequest) {
        Flight newFlight = flightBuilder.buildFlight(createFlightRequest);
        Optional<Flight> optionalFlight = flightRepository.findByFlightCode(newFlight.getFlightCode());
        if (!optionalFlight.isEmpty()) {
            log.error("Flight with code {} already exists", newFlight.getFlightCode());
            throw new FlightNumberAlreadyExists(ErrorStatus.FLIGHT_ALREADY_EXISTS);
        }
        flightRepository.save(newFlight);
        log.info("Flight saved successfully!");
        return flightConverter.apply(newFlight);
    }

    @Transactional
    public void updateFlightInfo(String flightCode, UpdateFlightRequest updateFlightRequest) {
        Flight flight = getFlightByCode(flightCode);
        Flight updatedFlight = flightConverter.updateFlightInfo(flight, updateFlightRequest);
        flightRepository.save(updatedFlight);
        log.info("Flight saved successfully!");
    }

    @Transactional
    public void deleteFlight(String flightCode) {
        Flight flight = getFlightByCode(flightCode);
        flightRepository.delete(flight);
        log.info("Flight removed successfully!");
    }

    @Transactional(readOnly = true)
    public FlightInformationResponse getFlightInformation(String flightCode) {
        Flight flight = getFlightByCode(flightCode);
        return flightConverter.infoFlight(flight, flight.getSeats());
    }

    @Transactional(readOnly = true)
    public Flight getFlightByCode(String flightCode) {
        Optional<Flight> optionalFlight = flightRepository.findByFlightCode(flightCode);
        if (optionalFlight.isEmpty()) {
            throw new FlightNotFoundException(ErrorStatus.FLIGHT_NOT_FOUND);
        }
        return optionalFlight.get();
    }

    @Transactional(readOnly = true)
    public List<FlightResponse> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        return flights.stream()
                .map(flightConverter::apply)
                .collect(Collectors.toList());
    }
}