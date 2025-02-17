package com.flight_booking.controller;

import com.flight_booking.model.request.CreateSeatsRequest;
import com.flight_booking.model.response.seat.SeatInformationResponse;
import com.flight_booking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/{flightCode}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public void createSeat(@PathVariable String flightCode, @RequestBody CreateSeatsRequest createSeatsRequest) {
        seatService.createSeatForFlight(flightCode, createSeatsRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void updateSeats(@RequestBody List<String> seatNumbers) {
        seatService.updateSeat(seatNumbers);
    }

    @GetMapping("/{flightCode}")
    @PreAuthorize("hasAnyRole('PASSENGER', 'ADMIN')")
    public List<SeatInformationResponse> getSeatsByFlight(@PathVariable String flightCode) {
        return seatService.getSeatsByFlight(flightCode);
    }
}