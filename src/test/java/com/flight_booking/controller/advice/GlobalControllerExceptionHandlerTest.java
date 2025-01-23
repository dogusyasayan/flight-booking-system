package com.flight_booking.controller.advice;

import com.flight_booking.FlightBookingApplication;
import com.flight_booking.exception.FlightNotFoundException;
import com.flight_booking.exception.FlightNumberAlreadyExists;
import com.flight_booking.exception.InvalidDateTimeFormatException;
import com.flight_booking.exception.PaymentException;
import com.flight_booking.exception.SeatAlreadySold;
import com.flight_booking.exception.SeatNotFoundException;
import com.flight_booking.exception.enums.ErrorStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TestController.class)
@ContextConfiguration(classes = {FlightBookingApplication.class})
class GlobalControllerExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void it_should_respond_with_404_for_FlightNotFoundException() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/flight-not-found-exception"));

        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("FlightNotFoundException")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_404_for_FlightNumberAlreadyExists() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/flight-already-exist-exception"));

        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("FlightNumberAlreadyExists")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_404_for_SeatNotFoundException() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/seat-not-found-exception"));

        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("SeatNotFoundException")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_404_for_SeatAlreadySold() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/seat-already-sold"));

        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("SeatAlreadySold")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_404_for_PaymentException() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/payment-exception"));

        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("PaymentException")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void it_should_respond_with_404_for_InvalidDateTimeFormatException() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/invalid-date-time-format-exception"));

        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("InvalidDateTimeFormatException")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}

@RestController
class TestController {
    @GetMapping("/flight-not-found-exception")
    public void FlightNotFoundException() {
        throw new FlightNotFoundException(ErrorStatus.FLIGHT_NOT_FOUND);
    }

    @GetMapping("/flight-already-exist-exception")
    public void FlightNumberAlreadyExists() {
        throw new FlightNumberAlreadyExists(ErrorStatus.FLIGHT_ALREADY_EXITS);
    }

    @GetMapping("/seat-not-found-exception")
    public void SeatNotFoundException() {
        throw new SeatNotFoundException(ErrorStatus.SEAT_NOT_FOUND);
    }

    @GetMapping("/seat-already-sold")
    public void SeatAlreadySold() {
        throw new SeatAlreadySold(ErrorStatus.SEAT_ALREADY_SOLD);
    }

    @GetMapping("/payment-exception")
    public void PaymentException() {
        throw new PaymentException(ErrorStatus.PAYMENT_FAILED);
    }

    @GetMapping("/invalid-date-time-format-exception")
    public void InvalidDateTimeFormatException() {
        throw new InvalidDateTimeFormatException(ErrorStatus.INVALID_DATE_TIME_FORMAT);
    }
}