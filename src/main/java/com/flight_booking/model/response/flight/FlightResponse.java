package com.flight_booking.model.response.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightResponse {
    private String flightCode;
    private String name;
    private String description;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
}
