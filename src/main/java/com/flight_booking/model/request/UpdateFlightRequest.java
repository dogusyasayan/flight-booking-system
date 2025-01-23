package com.flight_booking.model.request;

import com.flight_booking.enums.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFlightRequest implements Serializable {
    private String name;
    private String description;
    private FlightStatus flightStatus;
    private String departureTime;
    private String arrivalTime;
}