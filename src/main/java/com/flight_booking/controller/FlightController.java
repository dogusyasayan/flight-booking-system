package com.flight_booking.controller;

import com.flight_booking.model.request.CreateFlightRequest;
import com.flight_booking.model.request.UpdateFlightRequest;
import com.flight_booking.model.response.flight.FlightInformationResponse;
import com.flight_booking.model.response.flight.FlightResponse;
import com.flight_booking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/{flightCode}")
    @PreAuthorize("hasAnyRole('PASSENGER', 'ADMIN')")
    public FlightInformationResponse getFlightInformation(@PathVariable String flightCode) {
        return flightService.getFlightInformation(flightCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public FlightResponse createFlight(@RequestBody CreateFlightRequest createFlightRequest) {
        return flightService.createFlight(createFlightRequest);
    }

    @DeleteMapping("/{flightCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFlight(@PathVariable String flightCode) {
        flightService.deleteFlight(flightCode);
    }

    @PutMapping("/{flightCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void updateFlightInfo(@PathVariable String flightCode, @RequestBody UpdateFlightRequest updateFlightRequest) {
        flightService.updateFlightInfo(flightCode, updateFlightRequest);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('PASSENGER', 'ADMIN')")
    public List<FlightResponse> getAllFlights() {
        return flightService.getAllFlights();
    }
}