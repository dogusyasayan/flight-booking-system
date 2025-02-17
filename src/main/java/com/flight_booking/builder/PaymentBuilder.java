package com.flight_booking.builder;

import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Payment;
import com.flight_booking.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentBuilder {

    public Payment build(Flight flight, BigDecimal totalPrice, List<String> seatNumbers) {
        return Payment.builder()
                .flight(flight)
                .price(totalPrice)
                .status(PaymentStatus.SUCCESS)
                .seatNumbers(seatNumbers)
                .build();
    }
}
