package com.flight_booking.service;

import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.PaymentStatus;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.exception.PaymentException;
import com.flight_booking.exception.enums.ErrorStatus;
import com.flight_booking.model.request.payment.PaymentRequest;
import com.flight_booking.model.response.payment.PaymentResponse;
import com.flight_booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final FlightService flightService;
    private final SeatService seatService;
    private final SeatRepository seatRepository;

    @Transactional
    public synchronized PaymentResponse createPayment(Long flightId, PaymentRequest paymentRequest) {
        Flight flight = flightService.getFlightById(flightId);
        List<String> requestedSeatNumbers = paymentRequest.getSeatNumbers();
        Set<Seat> flightSeats = flight.getSeats();
        if (!areSeatsAvailable(flightSeats, requestedSeatNumbers)) {
            log.error("Seats {} are already sold or not available for flight {}", requestedSeatNumbers, flightId);
            throw new PaymentException(ErrorStatus.SEAT_ALREADY_SOLD);
        }
        reserveSeats(flightSeats, requestedSeatNumbers);
        PaymentStatus paymentStatus = PaymentStatus.SUCCESS;
        log.info("Payment successful for seats {} on flight {}", requestedSeatNumbers, flightId);

        return PaymentResponse.builder()
                .flightInformationResponse(flightService.getFlightInformation(flightId))
                .seatNumbers(requestedSeatNumbers)
                .paymentStatus(paymentStatus)
                .build();
    }

    private boolean areSeatsAvailable(Set<Seat> seats, List<String> requestedSeatNumbers) {
        Set<String> availableSeatNumbers = seats.stream()
                .filter(seat -> SeatStatus.AVAILABLE.equals(seat.getSeatStatus()))
                .map(Seat::getSeatNumber)
                .collect(Collectors.toSet());
        return availableSeatNumbers.containsAll(requestedSeatNumbers);
    }

    private void reserveSeats(Set<Seat> seats, List<String> requestedSeatNumbers) {
        seats.stream()
                .filter(seat -> requestedSeatNumbers.contains(seat.getSeatNumber()))
                .forEach(seat -> {
                    seat.setSeatStatus(SeatStatus.SOLD);
                    seatRepository.save(seat);
                    log.info("Seat {} reserved successfully.", seat.getSeatNumber());
                });
    }
}
