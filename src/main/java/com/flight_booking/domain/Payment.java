package com.flight_booking.domain;

import com.flight_booking.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Flight flight;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ElementCollection
    private List<String> seatNumbers;
}
