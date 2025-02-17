package com.flight_booking.converter;

import com.flight_booking.domain.Payment;
import com.flight_booking.model.response.flight.FlightInformationResponse;
import com.flight_booking.model.response.payment.PaymentResponse;
import com.flight_booking.model.response.seat.SeatInformationResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PaymentConverter {

    public PaymentResponse apply(Payment payment) {
        return PaymentResponse.builder()
                .flightInformationResponse(
                        FlightInformationResponse.builder()
                                .flightCode(payment.getFlight().getFlightCode())
                                .name(payment.getFlight().getName())
                                .description(payment.getFlight().getDescription())
                                .informationResponses(payment.getFlight().getSeats().stream()
                                        .map(seat -> SeatInformationResponse.builder()
                                                .seatNumber(seat.getSeatNumber())
                                                .seatStatus(seat.getSeatStatus().getValue())
                                                .seatPrice(seat.getSeatPrice())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build()
                )
                .seatNumbers(payment.getSeatNumbers())
                .paymentStatus(payment.getStatus())
                .price(payment.getPrice())
                .build();
    }
}
