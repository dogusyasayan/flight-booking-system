package com.flight_booking.controller;

import com.flight_booking.model.request.CreateFlightRequest;
import com.flight_booking.model.request.UpdateFlightRequest;
import com.flight_booking.model.response.flight.FlightInformationResponse;
import com.flight_booking.model.response.flight.FlightResponse;
import com.flight_booking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/{flightId}")
    @ResponseStatus(HttpStatus.OK)
    public FlightInformationResponse getFlightInformation(@PathVariable Long flightId) {
        return flightService.getFlightInformation(flightId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightResponse createFlight(@RequestBody CreateFlightRequest createFlightRequest) {
        return flightService.createFlight(createFlightRequest);
    }

    @DeleteMapping("/{flightId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFlight(@PathVariable Long flightId) {
        flightService.deleteFlight(flightId);
    }

    @PutMapping("/{flightId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFlightInfo(@PathVariable Long flightId, @RequestBody UpdateFlightRequest updateCartItemRequest) {
        flightService.updateFlightInfo(flightId, updateCartItemRequest);
    }
}