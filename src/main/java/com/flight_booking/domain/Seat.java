package com.flight_booking.domain;

import com.flight_booking.enums.SeatStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seat")
public class Seat extends BaseEntity {

    @Id
    @Column(name = "seat_number")
    private String seatNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @Column(name = "seat_price")
    private BigDecimal seatPrice;


    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
}
