package com.flight_booking.builder;

import com.flight_booking.domain.Flight;
import com.flight_booking.enums.FlightStatus;
import com.flight_booking.exception.InvalidDateTimeFormatException;
import com.flight_booking.model.request.CreateFlightRequest;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@ExtendWith(MockitoExtension.class)
class FlightBuilderTest {

    @InjectMocks
    private FlightBuilder flightBuilder;

    @Test
    void it_should_build() {
        //given
        CreateFlightRequest request = CreateFlightRequest.builder()
                .name("name")
                .description("description")
                .departureTime("1997-11-14 10:00")
                .arrivalTime("1997-11-14 12:00")
                .build();

        //when
        Flight result = flightBuilder.buildFlight(request);

        //then
        assertThat(result.getName()).isEqualTo("name");
        assertThat(result.getDescription()).isEqualTo("description");
        AssertionsForInterfaceTypes.assertThat(result.getFlightStatus()).isEqualTo(FlightStatus.UPCOMING);
        assertThat(result.getDepartureTime()).isEqualTo(LocalDateTime.of(1997, 11, 14, 10, 00));
        assertThat(result.getArrivalTime()).isEqualTo(LocalDateTime.of(1997, 11, 14, 12, 00));
    }

    @Test
    void it_should_throw_exception_when_invalid_date_time_format_build_flight() {
        //given
        CreateFlightRequest createFlightRequest = CreateFlightRequest.builder()
                .name("name")
                .description("description")
                .departureTime("ex")
                .arrivalTime("ex")
                .build();

        //when
        InvalidDateTimeFormatException thrown = (InvalidDateTimeFormatException) catchThrowable(() -> flightBuilder.buildFlight(createFlightRequest));

        //then
        assertThat(thrown).isInstanceOf(InvalidDateTimeFormatException.class);
    }
}