package com.flight_booking.controller;

import com.flight_booking.model.request.CreateFlightRequest;
import com.flight_booking.model.request.UpdateFlightRequest;
import com.flight_booking.service.FlightService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Test
    @WithMockUser(roles = {"ADMIN", "PASSENGER"})
    void it_should_get_flight_information() throws Exception {
        //given
        String flightCode = "TK123";

        //when
        ResultActions resultActions = mockMvc.perform(get("/flight/" + flightCode));

        //then
        resultActions.andExpect(status().isOk());
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(flightService).getFlightInformation(argumentCaptor.capture());
        String capturedFlightCode = argumentCaptor.getValue();
        assertThat(capturedFlightCode).isEqualTo(flightCode);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void it_should_create_flight() throws Exception {
        //given
        CreateFlightRequest request = CreateFlightRequest.builder().build();

        //when
        ResultActions resultActions = mockMvc.perform(post("/flight")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(request)));

        //then
        resultActions.andExpect(status().isCreated());
        ArgumentCaptor<CreateFlightRequest> argumentCaptor = ArgumentCaptor.forClass(CreateFlightRequest.class);
        verify(flightService).createFlight(argumentCaptor.capture());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void it_should_update_flight_info() throws Exception {
        //given
        String flightCode = "TK123";
        UpdateFlightRequest request = UpdateFlightRequest.builder().build();

        //when
        ResultActions resultActions = mockMvc.perform(put("/flight/" + flightCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(request)));

        //then
        resultActions.andExpect(status().isNoContent());
        ArgumentCaptor<String> argumentCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UpdateFlightRequest> argumentCaptor2 = ArgumentCaptor.forClass(UpdateFlightRequest.class);
        verify(flightService).updateFlightInfo(argumentCaptor1.capture(), argumentCaptor2.capture());
        String capturedFlightCode = argumentCaptor1.getValue();
        assertThat(capturedFlightCode).isEqualTo(flightCode);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void it_should_delete_flight() throws Exception {
        //given
        String flightCode = "TK123";

        //when
        ResultActions resultActions = mockMvc.perform(delete("/flight/" + flightCode));

        //then
        resultActions.andExpect(status().isNoContent());
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(flightService).deleteFlight(argumentCaptor.capture());
        String capturedFlightCode = argumentCaptor.getValue();
        assertThat(capturedFlightCode).isEqualTo(flightCode);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "PASSENGER"})
    void it_should_get_all_flights() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(get("/flight/all"));

        //then
        resultActions.andExpect(status().isOk());
        verify(flightService).getAllFlights();
    }
}