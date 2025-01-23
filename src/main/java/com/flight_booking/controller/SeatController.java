package com.flight_booking.controller;

import com.flight_booking.model.request.CreateSeatsRequest;
import com.flight_booking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/{flightId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSeat(@PathVariable Long flightId, @RequestBody CreateSeatsRequest createSeatsRequest) {
        seatService.createSeatForFlight(flightId, createSeatsRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeats(@RequestBody List<String> seatNumbers) {
        seatService.updateSeat(seatNumbers);
    }
}
