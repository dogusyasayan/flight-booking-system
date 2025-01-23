package com.flight_booking.service;

import com.flight_booking.builder.SeatBuilder;
import com.flight_booking.converter.SeatConverter;
import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.exception.FlightNotFoundException;
import com.flight_booking.model.request.CreateSeatsRequest;
import com.flight_booking.repository.FlightRepository;
import com.flight_booking.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
    private FlightRepository flightRepository;

    @Test
    void it_should_create_seats_for_flight() {
        //given
        Long flightId = 1L;
        Flight flight = Flight.builder().id(flightId).build();
        CreateSeatsRequest createSeatsRequest = CreateSeatsRequest.builder().build();
        List<Seat> seats = Arrays.asList(
                Seat.builder().flight(flight).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().flight(flight).seatStatus(SeatStatus.AVAILABLE).build()
        );

        given(flightRepository.findById(flightId)).willReturn(Optional.of(flight));
        given(seatBuilder.build(flight, createSeatsRequest)).willReturn(seats);

        //when
        seatService.createSeatForFlight(flightId, createSeatsRequest);

        //then
        verify(seatRepository).saveAll(seats);
    }

    @Test
    void it_should_throw_exception_when_flight_not_found() {
        //given
        Long flightId = 1L;
        CreateSeatsRequest createSeatsRequest = CreateSeatsRequest.builder().build();
        given(flightRepository.findById(flightId)).willReturn(Optional.empty());

        //when
        FlightNotFoundException thrown = (FlightNotFoundException) catchThrowable(() -> seatService.createSeatForFlight(flightId, createSeatsRequest));

        //then
        assertThat(thrown).isInstanceOf(FlightNotFoundException.class);
    }

    @Test
    void it_should_update_seat_status() {
        //given
        List<String> seatNumbers = Arrays.asList("A1", "A2");
        List<Seat> seats = Arrays.asList(
                Seat.builder().seatNumber("A1").seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().seatNumber("A2").seatStatus(SeatStatus.AVAILABLE).build()
        );

        given(seatRepository.findAllBySeatNumberIn(seatNumbers)).willReturn(seats);

        //when
        seatService.updateSeat(seatNumbers);

        //then
        verify(seatConverter).updateSeatStatus(seats);
        verify(seatRepository).saveAll(seats);
    }
}