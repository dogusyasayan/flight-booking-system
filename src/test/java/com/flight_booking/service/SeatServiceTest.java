package com.flight_booking.service;

import com.flight_booking.builder.SeatBuilder;
import com.flight_booking.converter.SeatConverter;
import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.exception.FlightNotFoundException;
import com.flight_booking.exception.enums.ErrorStatus;
import com.flight_booking.model.request.CreateSeatsRequest;
import com.flight_booking.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatBuilder seatBuilder;

    @Mock
    private SeatConverter seatConverter;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private FlightService flightService;

    @Test
    void it_should_create_seats_for_flight() {
        // given
        String flightCode = "TK2025";

        Flight flight = Flight.builder()
                .flightCode(flightCode)
                .build();

        List<Seat> existingSeats = new ArrayList<>(List.of(
                Seat.builder().seatNumber("1A").seatPrice(BigDecimal.valueOf(100)).seatStatus(SeatStatus.AVAILABLE).flight(flight).build(),
                Seat.builder().seatNumber("1B").seatPrice(BigDecimal.valueOf(150)).seatStatus(SeatStatus.AVAILABLE).flight(flight).build()
        ));

        CreateSeatsRequest createSeatsRequest = CreateSeatsRequest.builder()
                .numberOfSeats(2)
                .seatPrice(BigDecimal.valueOf(100))
                .build();

        given(flightService.getFlightByCode(flightCode)).willReturn(flight);
        given(seatRepository.findByFlight(flight)).willReturn(existingSeats);
        given(seatBuilder.build(flight, createSeatsRequest, existingSeats.size())).willReturn(existingSeats);

        // when
        seatService.createSeatForFlight(flightCode, createSeatsRequest);

        // then
        verify(seatRepository).saveAll(existingSeats);
    }


    @Test
    void it_should_throw_exception_when_flight_not_found() {
        // given
        String flightCode = "TK123";
        CreateSeatsRequest createSeatsRequest = CreateSeatsRequest.builder().build();

        given(flightService.getFlightByCode(flightCode))
                .willThrow(new FlightNotFoundException(ErrorStatus.FLIGHT_NOT_FOUND));

        // when
        Throwable thrown = catchThrowable(() -> seatService.createSeatForFlight(flightCode, createSeatsRequest));

        // then
        assertThat(thrown).isInstanceOf(FlightNotFoundException.class);
    }


    @Test
    void it_should_update_seat_status() {
        // given
        List<String> seatNumbers = List.of("1A", "1B");

        List<Seat> seats = List.of(
                Seat.builder().seatNumber("1A").seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().seatNumber("1B").seatStatus(SeatStatus.AVAILABLE).build()
        );

        given(seatRepository.findAllBySeatNumberIn(seatNumbers)).willReturn(seats);

        // when
        seatService.updateSeat(seatNumbers);

        // then
        verify(seatConverter).updateSeatStatus(seats);
        verify(seatRepository).saveAll(seats);
    }
}
