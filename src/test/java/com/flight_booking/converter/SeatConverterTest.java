package com.flight_booking.converter;

import com.flight_booking.domain.Seat;
import com.flight_booking.enums.SeatStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class SeatConverterTest {

    @InjectMocks
    private SeatConverter seatConverter;

    @Test
    void it_should_update_seat_status_to_sold() {
        // given
        List<Seat> seatList = new ArrayList<>();
        seatList.add(new Seat());
        seatList.add(new Seat());
        seatList.add(new Seat());

        // when
        seatConverter.updateSeatStatus(seatList);

        // then
        assertThat(seatList).allMatch(seat -> seat.getSeatStatus() == SeatStatus.SOLD);
    }
}