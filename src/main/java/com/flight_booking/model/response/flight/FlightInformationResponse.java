package com.flight_booking.model.response.flight;

import com.flight_booking.model.response.seat.SeatInformationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightInformationResponse {
    private String flightCode;
    private String name;
    private String description;
    private List<SeatInformationResponse> informationResponses;
}
