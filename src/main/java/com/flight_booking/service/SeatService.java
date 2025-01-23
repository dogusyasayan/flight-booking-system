package com.flight_booking.service;

import com.flight_booking.builder.SeatBuilder;
import com.flight_booking.converter.SeatConverter;
import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.exception.FlightNotFoundException;
import com.flight_booking.exception.enums.ErrorStatus;
import com.flight_booking.model.request.CreateSeatsRequest;
import com.flight_booking.repository.FlightRepository;
import com.flight_booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private final SeatBuilder seatBuilder;
    private final SeatConverter seatConverter;
    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;

    @Transactional
    public void createSeatForFlight(Long flightId, CreateSeatsRequest createSeatsRequest) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(ErrorStatus.FLIGHT_NOT_FOUND));
        List<Seat> seats = seatBuilder.build(flight, createSeatsRequest);
        seatRepository.saveAll(seats);
        log.info("Seats have been created for flight: {}", flight);
    }

    @Transactional
    public void updateSeat(List<String> seatNumbers) {
        List<Seat> seatList = seatRepository.findAllBySeatNumberIn(seatNumbers);
        seatConverter.updateSeatStatus(seatList);
        seatRepository.saveAll(seatList);
        log.info("Flight saved successfully!");
    }
}
