package com.flight_booking.converter;

import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.FlightStatus;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.model.request.UpdateFlightRequest;
import com.flight_booking.model.response.flight.FlightInformationResponse;
import com.flight_booking.model.response.flight.FlightResponse;
import com.flight_booking.model.response.seat.SeatInformationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class FlightConverterTest {

    @InjectMocks
    private FlightConverter flightConverter;

    @Test
    void it_should_apply() {
        //given
        Flight flight = Flight.builder()
                .flightCode("TK1997")
                .name("rotterdam")
                .description("abroad")
                .departureTime(LocalDateTime.of(1997, 11, 14, 10, 00))
                .arrivalTime(LocalDateTime.of(1997, 11, 14, 12, 00))
                .build();

        //when
        FlightResponse result = flightConverter.apply(flight);

        //then
        assertThat(result)
                .extracting(
                        FlightResponse::getFlightCode,
                        FlightResponse::getName,
                        FlightResponse::getDescription,
                        FlightResponse::getFormattedDepartureTime,
                        FlightResponse::getFormattedArrivalTime
                )
                .containsExactly(
                        "TK1997",
                        "rotterdam",
                        "abroad",
                        "14.11.1997 10:00", // Beklenen format
                        "14.11.1997 12:00"  // Beklenen format
                );
    }


    @Test
    void it_should_update_flight_info() {
        //given
        Flight flight = Flight.builder()
                .flightCode("TK1997")
                .name("rotterdam")
                .description("abroad")
                .departureTime(LocalDateTime.of(1997, 11, 14, 10, 00))
                .arrivalTime(LocalDateTime.of(1997, 11, 14, 12, 00))
                .build();

        UpdateFlightRequest updateFlightRequest = UpdateFlightRequest.builder()
                .name("istanbul")
                .description("domestic")
                .departureTime("2004-03-01 10:30")
                .arrivalTime("2004-03-01 12:30")
                .build();

        //when
        Flight result = flightConverter.updateFlightInfo(flight, updateFlightRequest);

        //then
        assertThat(result)
                .extracting(
                        Flight::getFlightCode,
                        Flight::getName,
                        Flight::getDescription,
                        Flight::getDepartureTime,
                        Flight::getArrivalTime

                )
                .containsExactly(
                        "TK1997",
                        "istanbul",
                        "domestic",
                        LocalDateTime.of(2004, 03, 01, 10, 30),
                        LocalDateTime.of(2004, 03, 01, 12, 30)
                );
    }

    @Test
    void it_should_create_flight_information_response() {
        //given
        Flight flight = Flight.builder()
                .flightCode("TK1997")
                .name("Istanbul to Amsterdam")
                .description("Direct flight")
                .departureTime(LocalDateTime.of(2024, 4, 25, 12, 0))
                .arrivalTime(LocalDateTime.of(2024, 4, 25, 14, 0))
                .flightStatus(FlightStatus.UPCOMING)
                .build();

        Set<Seat> seats = new HashSet<>();
        seats.add(Seat.builder()
                .seatNumber("A1")
                .seatStatus(SeatStatus.AVAILABLE)
                .seatPrice(BigDecimal.valueOf(100))
                .flight(flight)
                .build());

        seats.add(Seat.builder()
                .seatNumber("A2")
                .seatStatus(SeatStatus.SOLD)
                .seatPrice(BigDecimal.valueOf(120))
                .flight(flight)
                .build());

        //when
        FlightInformationResponse flightInformationResponse = flightConverter.infoFlight(flight, seats);

        //then
        assertThat(flightInformationResponse).isNotNull();
        assertThat(flightInformationResponse.getFlightCode()).isEqualTo("TK1997");
        assertThat(flightInformationResponse.getName()).isEqualTo("Istanbul to Amsterdam");
        assertThat(flightInformationResponse.getDescription()).isEqualTo("Direct flight");

        assertThat(flightInformationResponse.getInformationResponses().get(0).getSeatNumber()).isEqualTo("A1");
        assertThat(flightInformationResponse.getInformationResponses().get(0).getSeatStatus()).isEqualTo("Available");
        assertThat(flightInformationResponse.getInformationResponses().get(0).getSeatPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));

        assertThat(flightInformationResponse.getInformationResponses().get(1).getSeatNumber()).isEqualTo("A2");
        assertThat(flightInformationResponse.getInformationResponses().get(1).getSeatStatus()).isEqualTo("Sold");
        assertThat(flightInformationResponse.getInformationResponses().get(1).getSeatPrice()).isEqualByComparingTo(BigDecimal.valueOf(120));
    }

    @Test
    void it_should_convert_seat_information_to_list_of_seat_information_responses() {
        //given
        Set<Seat> seats = new HashSet<>();
        seats.add(Seat.builder()
                .seatNumber("A1")
                .seatStatus(SeatStatus.AVAILABLE)
                .seatPrice(BigDecimal.valueOf(100))
                .build());

        seats.add(Seat.builder()
                .seatNumber("A2")
                .seatStatus(SeatStatus.SOLD)
                .seatPrice(BigDecimal.valueOf(120))
                .build());

        // when
        List<SeatInformationResponse> seatInformationResponses = flightConverter.getSeatStatusInfo(seats);

        // then
        assertThat(seatInformationResponses).isNotNull();

        assertThat(seatInformationResponses.get(0).getSeatNumber()).isEqualTo("A1");
        assertThat(seatInformationResponses.get(0).getSeatStatus()).isEqualTo("Available");
        assertThat(seatInformationResponses.get(0).getSeatPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));

        assertThat(seatInformationResponses.get(1).getSeatNumber()).isEqualTo("A2");
        assertThat(seatInformationResponses.get(1).getSeatStatus()).isEqualTo("Sold");
        assertThat(seatInformationResponses.get(1).getSeatPrice()).isEqualByComparingTo(BigDecimal.valueOf(120));
    }
}
