package com.flight_booking.repository;

import com.flight_booking.domain.Seat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class SeatRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SeatRepository seatRepository;

    @Test
    public void it_should_find_all_by_seat_numbers_in() {
        //given
        Seat seat1 = new Seat();
        seat1.setSeatNumber("A1");
        seat1.setSeatPrice(BigDecimal.valueOf(100));
        Seat seat2 = new Seat();
        seat2.setSeatNumber("A2");
        seat2.setSeatPrice(BigDecimal.valueOf(120));
        entityManager.persistAndFlush(seat1);
        entityManager.persistAndFlush(seat2);

        //when
        List<Seat> result = seatRepository.findAllBySeatNumberIn(Arrays.asList("A1", "A2"));

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getSeatNumber()).isEqualTo(seat1.getSeatNumber());
        assertThat(result.get(1).getSeatNumber()).isEqualTo(seat2.getSeatNumber());
    }
}