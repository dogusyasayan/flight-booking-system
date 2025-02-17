package com.flight_booking.builder;

import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Payment;
import com.flight_booking.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentBuilderTest {

    private final PaymentBuilder paymentBuilder = new PaymentBuilder();

    @Test
    void it_should_build_payment_successfully() {
        // given
        Flight flight = Flight.builder()
                .flightCode("TK2025")
                .build();

        BigDecimal totalPrice = BigDecimal.valueOf(250);
        List<String> seatNumbers = List.of("1A", "1B");

        // when
        Payment payment = paymentBuilder.build(flight, totalPrice, seatNumbers);

        // then
        assertThat(payment).isNotNull();
        assertThat(payment.getFlight()).isEqualTo(flight);
        assertThat(payment.getPrice()).isEqualTo(totalPrice);
        assertThat(payment.getSeatNumbers()).isEqualTo(seatNumbers);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }
}
