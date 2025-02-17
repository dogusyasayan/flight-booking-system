package com.flight_booking.controller;

import com.flight_booking.model.request.CreateSeatsRequest;
import com.flight_booking.model.response.seat.SeatInformationResponse;
import com.flight_booking.service.SeatService;
import com.flight_booking.testutils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatService seatService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void it_should_create_seat_for_flight() throws Exception {
        // given
        String flightCode = "TK123";
        CreateSeatsRequest createSeatsRequest = CreateSeatsRequest.builder()
                .numberOfSeats(2)
                .seatPrice(BigDecimal.valueOf(100))
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(post("/seat/" + flightCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(createSeatsRequest)));

        // then
        resultActions.andExpect(status().isCreated());
        ArgumentCaptor<String> flightCodeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CreateSeatsRequest> requestCaptor = ArgumentCaptor.forClass(CreateSeatsRequest.class);
        verify(seatService).createSeatForFlight(flightCodeCaptor.capture(), requestCaptor.capture());

        assertThat(flightCodeCaptor.getValue()).isEqualTo(flightCode);
        assertThat(requestCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(createSeatsRequest);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void it_should_update_seats() throws Exception {
        // given
        List<String> seatNumbers = Arrays.asList("1A", "2B", "3C");

        // when
        ResultActions resultActions = mockMvc.perform(put("/seat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(seatNumbers)));

        // then
        resultActions.andExpect(status().isNoContent());
        ArgumentCaptor<List<String>> seatCaptor = ArgumentCaptor.forClass(List.class);
        verify(seatService).updateSeat(seatCaptor.capture());

        assertThat(seatCaptor.getValue()).isEqualTo(seatNumbers);
    }

    @Test
    @WithMockUser(roles = {"PASSENGER", "ADMIN"})
    void it_should_get_seats_by_flight() throws Exception {
        // given
        String flightCode = "TK123";
        List<SeatInformationResponse> mockSeats = List.of(
                new SeatInformationResponse("1A", "AVAILABLE", BigDecimal.valueOf(100)),
                new SeatInformationResponse("2B", "RESERVED", BigDecimal.valueOf(100))
        );

        when(seatService.getSeatsByFlight(flightCode)).thenReturn(mockSeats);

        // when
        ResultActions resultActions = mockMvc.perform(get("/seat/" + flightCode));

        // then
        resultActions.andExpect(status().isOk());
        ArgumentCaptor<String> flightCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(seatService).getSeatsByFlight(flightCodeCaptor.capture());

        assertThat(flightCodeCaptor.getValue()).isEqualTo(flightCode);
    }
}
