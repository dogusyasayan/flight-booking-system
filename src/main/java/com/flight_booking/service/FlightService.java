package com.flight_booking.service;

import com.flight_booking.builder.FlightBuilder;
import com.flight_booking.converter.FlightConverter;
import com.flight_booking.domain.Flight;
import com.flight_booking.exception.FlightNotFoundException;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightBuilder flightBuilder;
    private final FlightConverter flightConverter;

    @Transactional(readOnly = true)
    public FlightInformationResponse getFlightInformation(Long flightId) {
        Flight flight = getFlightById(flightId);
        return flightConverter.infoFlight(flight, flight.getSeats());
    }

    @Transactional
    public FlightResponse createFlight(CreateFlightRequest createFlightRequest) {
        Flight newFlight = flightBuilder.buildFlight(createFlightRequest);
        Optional<Flight> optionalFlight = flightRepository.findByFlightCode(newFlight.getFlightCode());
        if (!optionalFlight.isEmpty()) {
            log.error("Flight with code {} already exists", newFlight.getFlightCode());
            throw new FlightNotFoundException(ErrorStatus.FLIGHT_NOT_FOUND);
        }
        flightRepository.save(newFlight);
        log.info("Flight saved successfully!");
        return flightConverter.apply(newFlight);
    }

    @Transactional
    public void deleteFlight(Long flightId) {
        Flight flight = getFlightById(flightId);
        flightRepository.delete(flight);
        log.info("Flight removed successfully!");
    }

    @Transactional(readOnly = true)
    public Flight getFlightById(Long flightId) {
        Optional<Flight> optionalFlight = flightRepository.findById(flightId);
        if (optionalFlight.isEmpty()) {
            throw new FlightNotFoundException(ErrorStatus.FLIGHT_NOT_FOUND);
        }
        return optionalFlight.get();
    }

    @Transactional
    public void updateFlightInfo(Long flightId, UpdateFlightRequest updateFlightRequest) {
        Flight flight = getFlightById(flightId);
        Flight updatedFlight = flightConverter.updateFlightInfo(flight, updateFlightRequest);
        flightRepository.save(updatedFlight);
        log.info("Flight saved successfully!");
    }
}
