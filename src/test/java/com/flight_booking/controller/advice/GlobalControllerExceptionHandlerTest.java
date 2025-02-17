package com.flight_booking.controller.advice;

import com.flight_booking.exception.FlightAlreadyDepartedException;
import com.flight_booking.exception.FlightNotFoundException;
import com.flight_booking.exception.FlightNumberAlreadyExists;
import com.flight_booking.exception.InvalidDateTimeFormatException;
import com.flight_booking.exception.PaymentException;
import com.flight_booking.exception.SeatAlreadySold;
import com.flight_booking.exception.SeatNotFoundException;
import com.flight_booking.exception.enums.ErrorStatus;
import com.flight_booking.service.auth.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = TestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(GlobalControllerExceptionHandler.class)
class GlobalControllerExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void it_should_respond_with_404_for_FlightNotFoundException() throws Exception {
        mockMvc.perform(get("/flight-not-found-exception"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("FlightNotFoundException")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_409_for_FlightNumberAlreadyExists() throws Exception {
        mockMvc.perform(get("/flight-already-exist-exception"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.exception", is("FlightNumberAlreadyExists")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_404_for_SeatNotFoundException() throws Exception {
        mockMvc.perform(get("/seat-not-found-exception"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("SeatNotFoundException")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_409_for_SeatAlreadySold() throws Exception {
        mockMvc.perform(get("/seat-already-sold"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.exception", is("SeatAlreadySold")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_400_for_PaymentException() throws Exception {
        mockMvc.perform(get("/payment-exception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("PaymentException")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_400_for_InvalidDateTimeFormatException() throws Exception {
        mockMvc.perform(get("/invalid-date-time-format-exception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("InvalidDateTimeFormatException")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_400_for_FlightAlreadyDepartedException() throws Exception {
        mockMvc.perform(get("/flight-already-departed"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("FlightAlreadyDepartedException")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}


@RestController
class TestController {

    @GetMapping("/flight-not-found-exception")
    public void flightNotFoundException() {
        throw new FlightNotFoundException(ErrorStatus.FLIGHT_NOT_FOUND);
    }

    @GetMapping("/flight-already-exist-exception")
    public void flightNumberAlreadyExists() {
        throw new FlightNumberAlreadyExists(ErrorStatus.FLIGHT_ALREADY_EXISTS);
    }

    @GetMapping("/seat-not-found-exception")
    public void seatNotFoundException() {
        throw new SeatNotFoundException(ErrorStatus.SEAT_NOT_FOUND);
    }

    @GetMapping("/seat-already-sold")
    public void seatAlreadySold() {
        throw new SeatAlreadySold(ErrorStatus.SEAT_ALREADY_SOLD);
    }

    @GetMapping("/payment-exception")
    public void paymentException() {
        throw new PaymentException(ErrorStatus.PAYMENT_FAILED);
    }

    @GetMapping("/invalid-date-time-format-exception")
    public void invalidDateTimeFormatException() {
        throw new InvalidDateTimeFormatException(ErrorStatus.INVALID_DATE_TIME_FORMAT);
    }

    @GetMapping("/flight-already-departed")
    public void flightAlreadyDeparted() {
        throw new FlightAlreadyDepartedException(ErrorStatus.FLIGHT_ALREADY_DEPARTED);
    }
}