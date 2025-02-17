package com.flight_booking.service;

import com.flight_booking.builder.SeatBuilder;
import com.flight_booking.converter.SeatConverter;
import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.model.request.CreateSeatsRequest;
import com.flight_booking.model.response.seat.SeatInformationResponse;
import com.flight_booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private final SeatBuilder seatBuilder;
    private final SeatConverter seatConverter;
    private final SeatRepository seatRepository;
    private final FlightService flightService;


    @Transactional
    public void createSeatForFlight(String flightCode, CreateSeatsRequest createSeatsRequest) {
        Flight flight = flightService.getFlightByCode(flightCode);
        List<Seat> existingSeats = seatRepository.findByFlight(flight);
        int existingSeatCount = existingSeats.size();
        List<Seat> newSeats = seatBuilder.build(flight, createSeatsRequest, existingSeatCount);
        existingSeats.addAll(newSeats);
        seatRepository.saveAll(existingSeats);
        log.info("Added {} new seats to flight: {}, total seats: {}", createSeatsRequest.getNumberOfSeats(), flightCode, existingSeats.size());
    }

    @Transactional
    public void updateSeat(List<String> seatNumbers) {
        List<Seat> seatList = seatRepository.findAllBySeatNumberIn(seatNumbers);
        seatConverter.updateSeatStatus(seatList);
        seatRepository.saveAll(seatList);
        log.info("Flight saved successfully!");
    }

    public List<SeatInformationResponse> getSeatsByFlight(String flightCode) {
        List<Seat> seats = seatRepository.findByFlight_FlightCode(flightCode);

        return seats.stream()
                .map(seat -> new SeatInformationResponse(
                        seat.getSeatNumber(),
                        seat.getSeatStatus().getValue(),
                        seat.getSeatPrice()
                ))
                .collect(Collectors.toList());
    }
}