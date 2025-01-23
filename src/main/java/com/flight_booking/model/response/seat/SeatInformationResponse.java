package com.flight_booking.model.response.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatInformationResponse {
    private String seatNumber;
    private String seatStatus;
    private BigDecimal seatPrice;
}
