package com.flight_booking.service;

import com.flight_booking.builder.FlightBuilder;
import com.flight_booking.converter.FlightConverter;
import com.flight_booking.domain.Flight;
import com.flight_booking.enums.FlightStatus;
import com.flight_booking.exception.FlightNotFoundException;
import com.flight_booking.exception.FlightNumberAlreadyExists;
import com.flight_booking.model.request.CreateFlightRequest;
import com.flight_booking.model.request.UpdateFlightRequest;
import com.flight_booking.model.response.flight.FlightInformationResponse;
import com.flight_booking.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @InjectMocks
    private FlightService flightService;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightBuilder flightBuilder;
    @Mock
    private FlightConverter flightConverter;

    @Test
    void it_should_create_flight() {
        //given
        CreateFlightRequest createFlightRequest = CreateFlightRequest.builder()
                .name("name")
                .description("description")
                .build();
        Flight flight = Flight.builder()
                .flightCode("TK1997")
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        given(flightBuilder.buildFlight(createFlightRequest)).willReturn(flight);
        given(flightRepository.findByFlightCode(flight.getFlightCode())).willReturn(Optional.empty());

        //when
        flightService.createFlight(createFlightRequest);

        //then
        verify(flightRepository).save(flight);
        verify(flightConverter).apply(flight);
    }

    @Test
    void it_should_throw_exception_when_flight_is_not_empty_create_flight() {
        //given
        CreateFlightRequest createFlightRequest = CreateFlightRequest.builder()
                .name("name")
                .description("description")
                .build();
        Flight flight = Flight.builder()
                .flightCode("TK1997")
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        given(flightBuilder.buildFlight(createFlightRequest)).willReturn(flight);
        given(flightRepository.findByFlightCode(flight.getFlightCode())).willReturn(Optional.of(flight));

        //when
        FlightNumberAlreadyExists thrown = (FlightNumberAlreadyExists) catchThrowable(() -> flightService.createFlight(createFlightRequest));

        //then
        assertThat(thrown).isInstanceOf(FlightNumberAlreadyExists.class);
    }

    @Test
    void it_should_update_flight_info() {
        //given
        UpdateFlightRequest updateFlightRequest = new UpdateFlightRequest();
        Flight flight = Flight.builder()
                .id(1L)
                .flightCode("TK1997")
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        given(flightRepository.findById(1L)).willReturn(Optional.of(flight));
        given(flightConverter.updateFlightInfo(flight, updateFlightRequest)).willReturn(flight);

        //when
        flightService.updateFlightInfo(1L, updateFlightRequest);

        //then
        verify(flightRepository).save(flight);
    }

    @Test
    void it_should_delete_flight() {
        //given
        Long flightId = 1L;
        Flight flight = Flight.builder()
                .id(flightId)
                .flightCode("TK1997")
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        given(flightRepository.findById(flightId)).willReturn(Optional.of(flight));

        //when
        flightService.deleteFlight(flightId);

        //then
        verify(flightRepository).delete(flight);
    }

    @Test
    void it_should_get_flight_information() {
        //given
        Long flightId = 1L;
        Flight flight = Flight.builder()
                .id(flightId)
                .flightCode("TK1997")
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        given(flightRepository.findById(flightId)).willReturn(Optional.of(flight));
        given(flightConverter.infoFlight(flight, flight.getSeats())).willReturn(new FlightInformationResponse());

        //when
        FlightInformationResponse flightInformation = flightService.getFlightInformation(flightId);

        //then
        assertThat(flightInformation).isNotNull();
    }

    @Test
    void it_should_throw_exception_when_flight_not_found() {
        //given
        Long flightId = 1L;

        given(flightRepository.findById(flightId)).willReturn(Optional.empty());

        //when
        FlightNotFoundException thrown = (FlightNotFoundException) catchThrowable(() -> flightService.getFlightInformation(flightId));

        // then
        assertThat(thrown).isInstanceOf(FlightNotFoundException.class);
    }
}