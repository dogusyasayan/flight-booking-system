package com.flight_booking.repository;

import com.flight_booking.domain.Flight;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class FlightRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FlightRepository flightRepository;

    @Test
    public void it_should_find_by_flight_code() {
        // given
        Flight flight = new Flight();
        flight.setFlightCode("TK123");
        flight.setName("Test Flight");
        flight.setDescription("Test description");
        flight.setDepartureTime(LocalDateTime.now());
        flight.setArrivalTime(LocalDateTime.now().plusHours(2));
        entityManager.persistAndFlush(flight);

        // when
        Optional<Flight> result = flightRepository.findByFlightCode("TK123");

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getFlightCode()).isEqualTo(flight.getFlightCode());
    }
}