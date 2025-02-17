package com.flight_booking.service;

import com.flight_booking.builder.PaymentBuilder;
import com.flight_booking.converter.PaymentConverter;
import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Payment;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.exception.FlightAlreadyDepartedException;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final FlightService flightService;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentConverter paymentConverter;
    private final PaymentBuilder paymentBuilder;

    private final Lock paymentLock = new ReentrantLock();


    public BigDecimal calculateTotalPrice(String flightCode, List<String> seatNumbers) {
        Flight flight = flightService.getFlightByCode(flightCode);
        return flight.getSeats().stream()
                .filter(seat -> seatNumbers.contains(seat.getSeatNumber()))
                .map(Seat::getSeatPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public PaymentResponse createPayment(String flightCode, PaymentRequest paymentRequest) {
        paymentLock.lock();
        try {
            Flight flight = flightService.getFlightByCode(flightCode);

            if (flight.getDepartureTime().isBefore(java.time.LocalDateTime.now())) {
                log.error("🚨 Flight {} has already departed. Ticket cannot be purchased.", flightCode);
                throw new FlightAlreadyDepartedException(ErrorStatus.FLIGHT_ALREADY_DEPARTED);
            }

            List<String> requestedSeatNumbers = paymentRequest.getSeatNumbers();
            List<Seat> selectedSeats = seatRepository.findAllBySeatNumberIn(requestedSeatNumbers);

            if (selectedSeats.isEmpty() || selectedSeats.size() != requestedSeatNumbers.size()) {
                log.error("Requested seats {} are not available for flight {}", requestedSeatNumbers, flightCode);
                throw new PaymentException(ErrorStatus.SEAT_NOT_FOUND);
            }

            if (selectedSeats.stream().anyMatch(seat -> !SeatStatus.AVAILABLE.equals(seat.getSeatStatus()))) {
                log.error("Seats {} are already sold for flight {}", requestedSeatNumbers, flightCode);
                throw new PaymentException(ErrorStatus.SEAT_ALREADY_SOLD);
            }

            return processPayment(flight, selectedSeats, requestedSeatNumbers);

        } finally {
            paymentLock.unlock();
        }
    }

    private PaymentResponse processPayment(Flight flight, List<Seat> selectedSeats, List<String> seatNumbers) {
        try {
            BigDecimal totalPrice = selectedSeats.stream()
                    .map(Seat::getSeatPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            selectedSeats.forEach(seat -> seat.setSeatStatus(SeatStatus.SOLD));
            seatRepository.saveAll(selectedSeats);

            Payment payment = paymentBuilder.build(flight, totalPrice, seatNumbers);
            paymentRepository.save(payment);

            log.info("✅ Payment of {} processed successfully for flight {}", totalPrice, flight.getFlightCode());
            return paymentConverter.apply(payment);

        } catch (Exception e) {
            log.error("🚨 Payment failed for seats {} on flight {}", seatNumbers, flight.getFlightCode());
            throw new PaymentException(ErrorStatus.PAYMENT_FAILED);
        }
    }
}


