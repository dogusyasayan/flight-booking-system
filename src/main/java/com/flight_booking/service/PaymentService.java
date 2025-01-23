package com.flight_booking.service;

import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Payment;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.PaymentStatus;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.exception.PaymentException;
import com.flight_booking.exception.enums.ErrorStatus;
import com.flight_booking.model.request.payment.PaymentRequest;
import com.flight_booking.model.response.payment.PaymentResponse;
import com.flight_booking.repository.PaymentRepository;
import com.flight_booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final FlightService flightService;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;

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

        BigDecimal totalPrice = calculateTotalPrice(flightSeats, requestedSeatNumbers);

        Payment payment = Payment.builder()
                .flight(flight)
                .price(totalPrice)
                .build();
        paymentRepository.save(payment);

        log.info("Payment of {} saved for flight {}", totalPrice, flightId);

        return PaymentResponse.builder()
                .flightInformationResponse(flightService.getFlightInformation(flightId))
                .seatNumbers(requestedSeatNumbers)
                .paymentStatus(PaymentStatus.SUCCESS)
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
                    if (!SeatStatus.AVAILABLE.equals(seat.getSeatStatus())) {
                        throw new PaymentException(ErrorStatus.SEAT_ALREADY_SOLD);
                    }
                    seat.setSeatStatus(SeatStatus.RESERVED);
                    seatRepository.save(seat);
                    log.info("Seat {} reserved successfully.", seat.getSeatNumber());
                });
    }

    private BigDecimal calculateTotalPrice(Set<Seat> seats, List<String> requestedSeatNumbers) {
        return seats.stream()
                .filter(seat -> requestedSeatNumbers.contains(seat.getSeatNumber()))
                .map(Seat::getSeatPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
