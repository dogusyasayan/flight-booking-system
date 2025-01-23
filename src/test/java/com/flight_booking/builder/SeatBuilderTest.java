package com.flight_booking.builder;

import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.FlightStatus;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.model.request.CreateSeatsRequest;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class SeatBuilderTest {

    @InjectMocks
    private SeatBuilder seatBuilder;

    @Test
    void it_should_build_seats() {
        // given
        Flight flight = Flight.builder()
                .id(1L)
                .flightCode("FL001")
                .name("Test Flight")
                .description("Test Flight Description")
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        CreateSeatsRequest createSeatsRequest = CreateSeatsRequest.builder()
                .numberOfSeats(5)
                .seatPrice(BigDecimal.valueOf(50))
                .build();

        // when
        List<Seat> result = seatBuilder.build(flight, createSeatsRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(5);

        for (int i = 0; i < 5; i++) {
            Seat seat = result.get(i);
            AssertionsForClassTypes.assertThat(seat.getSeatNumber()).isEqualTo("FL001-" + (i + 1));
            assertThat(seat.getSeatStatus()).isEqualTo(SeatStatus.AVAILABLE);
            AssertionsForClassTypes.assertThat(seat.getSeatPrice()).isEqualByComparingTo(BigDecimal.valueOf(50));
            AssertionsForClassTypes.assertThat(seat.getFlight()).isEqualTo(flight);
        }
    }
}
