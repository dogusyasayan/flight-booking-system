package com.flight_booking.converter;

import com.flight_booking.domain.Seat;
import com.flight_booking.enums.SeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeatConverter {
    public void updateSeatStatus(List<Seat> seatList) {
        seatList.forEach(seat -> seat.setSeatStatus(SeatStatus.SOLD));
    }
}

