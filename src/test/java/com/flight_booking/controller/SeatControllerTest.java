package com.flight_booking.controller;

import com.flight_booking.model.request.CreateSeatsRequest;
import com.flight_booking.service.SeatService;
import com.flight_booking.testutils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
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
    void it_should_get_flight_information() throws Exception {
        //given
        CreateSeatsRequest createSeatsRequest = CreateSeatsRequest.builder().build();

        //when
        ResultActions resultActions = mockMvc.perform(post("/seat/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(createSeatsRequest))
        );

        //then
        resultActions.andExpect(status().isCreated());
        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<CreateSeatsRequest> argumentCaptor2 = ArgumentCaptor.forClass(CreateSeatsRequest.class);
        verify(seatService).createSeatForFlight(argumentCaptor1.capture(), argumentCaptor2.capture());
    }

    @Test
    void it_should_update_seats() throws Exception {
        //given
        List<String> seatNumbers = Arrays.asList("1A", "2B", "3C");

        //when
        ResultActions resultActions = mockMvc.perform(put("/seat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(seatNumbers))
        );

        //then
        resultActions.andExpect(status().isNoContent());
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(seatService).updateSeat(captor.capture());
        List<String> value = captor.getValue();
        assertThat(value).isEqualTo(seatNumbers);
    }
}