package com.flight_booking.model.response.payment;

import com.flight_booking.enums.PaymentStatus;
import com.flight_booking.model.response.flight.FlightInformationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse implements Serializable {

    private FlightInformationResponse flightInformationResponse;
    private List<String> seatNumbers;
    private PaymentStatus paymentStatus;
    private BigDecimal price;
}
