package com.flight_booking.service;

import com.flight_booking.builder.PaymentBuilder;
import com.flight_booking.converter.PaymentConverter;
import com.flight_booking.domain.Flight;
import com.flight_booking.domain.Payment;
import com.flight_booking.domain.Seat;
import com.flight_booking.enums.SeatStatus;
import com.flight_booking.exception.FlightAlreadyDepartedException;
import com.flight_booking.exception.PaymentException;
import com.flight_booking.model.request.payment.PaymentRequest;
import com.flight_booking.model.response.payment.PaymentResponse;
import com.flight_booking.repository.PaymentRepository;
import com.flight_booking.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentConverter paymentConverter;

    @Mock
    private PaymentBuilder paymentBuilder;

    private Flight flight;
    private List<Seat> availableSeats;
    private List<String> seatNumbers;
    private PaymentRequest paymentRequest;
    private Payment payment;

    @BeforeEach
    void setUp() {
        flight = Flight.builder()
                .flightCode("TK2025")
                .departureTime(LocalDateTime.now().plusDays(2))
                .build();

        availableSeats = List.of(
                Seat.builder().seatNumber("1A").seatPrice(BigDecimal.valueOf(100)).seatStatus(SeatStatus.AVAILABLE).flight(flight).build(),
                Seat.builder().seatNumber("1B").seatPrice(BigDecimal.valueOf(150)).seatStatus(SeatStatus.AVAILABLE).flight(flight).build()
        );

        flight.setSeats(new HashSet<>(availableSeats));

        seatNumbers = List.of("1A", "1B");

        paymentRequest = PaymentRequest.builder()
                .seatNumbers(seatNumbers)
                .build();

        payment = Payment.builder()
                .flight(flight)
                .price(BigDecimal.valueOf(250))
                .seatNumbers(seatNumbers)
                .build();
    }

    @Test
    void it_should_calculate_total_price() {
        // given
        given(flightService.getFlightByCode(flight.getFlightCode())).willReturn(flight);

        // when
        BigDecimal totalPrice = paymentService.calculateTotalPrice(flight.getFlightCode(), seatNumbers);

        // then
        assertThat(totalPrice).isEqualTo(BigDecimal.valueOf(250));
    }

    @Test
    void it_should_create_payment_successfully() {
        // given
        given(flightService.getFlightByCode(flight.getFlightCode())).willReturn(flight);
        given(seatRepository.findAllBySeatNumberIn(seatNumbers)).willReturn(availableSeats);
        given(paymentBuilder.build(flight, BigDecimal.valueOf(250), seatNumbers)).willReturn(payment);
        given(paymentConverter.apply(payment)).willReturn(mock(PaymentResponse.class));

        // when
        PaymentResponse response = paymentService.createPayment(flight.getFlightCode(), paymentRequest);

        // then
        assertThat(response).isNotNull();
        verify(seatRepository).saveAll(availableSeats);
        verify(paymentRepository).save(payment);
    }

    @Test
    void it_should_throw_exception_when_flight_is_departed() {
        // given
        flight.setDepartureTime(LocalDateTime.now().minusDays(1));
        given(flightService.getFlightByCode(flight.getFlightCode())).willReturn(flight);

        // when
        Throwable thrown = catchThrowable(() -> paymentService.createPayment(flight.getFlightCode(), paymentRequest));

        // then
        assertThat(thrown).isInstanceOf(FlightAlreadyDepartedException.class);
    }

    @Test
    void it_should_throw_exception_when_seats_are_already_sold() {
        // given
        availableSeats.get(0).setSeatStatus(SeatStatus.SOLD);
        given(flightService.getFlightByCode(flight.getFlightCode())).willReturn(flight);
        given(seatRepository.findAllBySeatNumberIn(seatNumbers)).willReturn(availableSeats);

        // when
        Throwable thrown = catchThrowable(() -> paymentService.createPayment(flight.getFlightCode(), paymentRequest));

        // then
        assertThat(thrown).isInstanceOf(PaymentException.class);
    }

    @Test
    void it_should_throw_exception_when_seats_are_not_found() {
        // given
        given(flightService.getFlightByCode(flight.getFlightCode())).willReturn(flight);
        given(seatRepository.findAllBySeatNumberIn(seatNumbers)).willReturn(List.of());

        // when
        Throwable thrown = catchThrowable(() -> paymentService.createPayment(flight.getFlightCode(), paymentRequest));

        // then
        assertThat(thrown).isInstanceOf(PaymentException.class);
    }

    @Test
    void it_should_throw_exception_when_payment_fails() {
        // given
        given(flightService.getFlightByCode(flight.getFlightCode())).willReturn(flight);
        given(seatRepository.findAllBySeatNumberIn(seatNumbers)).willReturn(availableSeats);
        doThrow(new RuntimeException()).when(paymentRepository).save(any());

        // when
        Throwable thrown = catchThrowable(() -> paymentService.createPayment(flight.getFlightCode(), paymentRequest));

        // then
        assertThat(thrown).isInstanceOf(PaymentException.class);
    }
}
