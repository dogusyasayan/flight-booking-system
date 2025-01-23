package com.flight_booking.service;

import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.exception.PaymentException;
import com.flight_booking.exception.enums.ErrorStatus;
import com.flight_booking.model.request.payment.PaymentRequest;
import com.flight_booking.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private FlightService flightService;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatService seatService;

    @Test
    void it_should_allow_only_one_successful_payment_for_same_seat() throws Exception {
        //given
        Long flightId = 1L;
        List<String> seatNumbers = Collections.singletonList("A1");

        Seat seat = Seat.builder()
                .seatNumber("A1")
                .seatStatus(SeatStatus.AVAILABLE)
                .build();

        Set<Seat> flightSeats = new HashSet<>(Collections.singletonList(seat));

        Flight flight = Flight.builder()
                .id(flightId)
                .seats(flightSeats)
                .build();

        given(flightService.getFlightById(flightId)).willReturn(flight);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .seatNumbers(seatNumbers)
                .build();

        //when
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<?> thread1 = executorService.submit(() -> {
            Throwable thrown = catchThrowable(() -> paymentService.createPayment(flightId, paymentRequest));
            assertThat(thrown).isNull();
        });

        Future<?> thread2 = executorService.submit(() -> {
            Throwable thrown = catchThrowable(() -> paymentService.createPayment(flightId, paymentRequest));
            assertThat(thrown)
                    .isInstanceOf(PaymentException.class)
                    .hasMessageContaining(ErrorStatus.SEAT_ALREADY_SOLD.toString());
        });

        thread1.get();
        thread2.get();

        //verify
        assertThat(seat.getSeatStatus()).isEqualTo(SeatStatus.RESERVED);
        verify(seatRepository, times(1)).save(seat);
    }
}