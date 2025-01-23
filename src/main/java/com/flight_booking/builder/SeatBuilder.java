package com.flight_booking.builder;

import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.model.request.CreateSeatsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatBuilder {

    public List<Seat> build(Flight flight, CreateSeatsRequest createSeatsRequest) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= createSeatsRequest.getNumberOfSeats(); i++) {
            Seat seat = Seat.builder()
                    .seatNumber(flight.getFlightCode() + "-" + i)
                    .seatStatus(SeatStatus.AVAILABLE)
                    .seatPrice(createSeatsRequest.getSeatPrice())
                    .flight(flight)
                    .build();
            seats.add(seat);
        }
        return seats;
    }
}
