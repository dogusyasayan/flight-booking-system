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
import com.flight_booking.model.response.flight.FlightResponse;
import com.flight_booking.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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

        flightService.createFlight(createFlightRequest);

        verify(flightRepository).save(flight);
        verify(flightConverter).apply(flight);
    }

    @Test
    void it_should_throw_exception_when_flight_is_not_empty_create_flight() {
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

        FlightNumberAlreadyExists thrown = (FlightNumberAlreadyExists) catchThrowable(() -> flightService.createFlight(createFlightRequest));

        assertThat(thrown).isInstanceOf(FlightNumberAlreadyExists.class);
    }

    @Test
    void it_should_update_flight_info() {
        String flightCode = "TK123";
        UpdateFlightRequest updateFlightRequest = new UpdateFlightRequest();
        Flight flight = Flight.builder()
                .flightCode(flightCode)
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        given(flightRepository.findByFlightCode(flightCode)).willReturn(Optional.of(flight));
        given(flightConverter.updateFlightInfo(flight, updateFlightRequest)).willReturn(flight);

        flightService.updateFlightInfo(flightCode, updateFlightRequest);

        verify(flightRepository).save(flight);
    }

    @Test
    void it_should_delete_flight() {
        String flightCode = "TK123";
        Flight flight = Flight.builder()
                .flightCode(flightCode)
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        given(flightRepository.findByFlightCode(flightCode)).willReturn(Optional.of(flight));

        flightService.deleteFlight(flightCode);

        verify(flightRepository).delete(flight);
    }

    @Test
    void it_should_get_flight_information() {
        String flightCode = "TK123";
        Flight flight = Flight.builder()
                .flightCode(flightCode)
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        given(flightRepository.findByFlightCode(flightCode)).willReturn(Optional.of(flight));
        given(flightConverter.infoFlight(flight, flight.getSeats())).willReturn(new FlightInformationResponse());

        FlightInformationResponse flightInformation = flightService.getFlightInformation(flightCode);

        assertThat(flightInformation).isNotNull();
    }

    @Test
    void it_should_throw_exception_when_flight_not_found() {
        String flightCode = "TK123";

        given(flightRepository.findByFlightCode(flightCode)).willReturn(Optional.empty());

        FlightNotFoundException thrown = (FlightNotFoundException) catchThrowable(() -> flightService.getFlightInformation(flightCode));

        assertThat(thrown).isInstanceOf(FlightNotFoundException.class);
    }

    @Test
    void it_should_get_all_flights() {
        // given
        FlightResponse flightResponse1 = FlightResponse.builder().build();
        FlightResponse flightResponse2 = FlightResponse.builder().build();

        Flight flight1 = Flight.builder().flightCode("TK1001").flightStatus(FlightStatus.UPCOMING).build();
        Flight flight2 = Flight.builder().flightCode("TK1002").flightStatus(FlightStatus.COMPLETED).build();
        List<Flight> flights = List.of(flight1, flight2);

        given(flightRepository.findAll()).willReturn(flights);
        given(flightConverter.apply(flight1)).willReturn(flightResponse1);
        given(flightConverter.apply(flight2)).willReturn(flightResponse2);

        // when
        List<FlightResponse> flightResponses = flightService.getAllFlights();

        // then
        assertThat(flightResponses).isNotNull();
        assertThat(flightResponses.size()).isEqualTo(2);
    }

}