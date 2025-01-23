package com.flight_booking.repository;

import com.flight_booking.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long>, JpaSpecificationExecutor<Seat> {

    Optional<Seat> findBySeatNumber(String seatNumber);

    List<Seat> findAllBySeatNumberIn(List<String> seatNumbers);
}

