package com.flight_booking.controller;

import com.flight_booking.domain.enums.Role;
import com.flight_booking.model.request.user.SignInRequest;
import com.flight_booking.model.request.user.SignUpRequest;
import com.flight_booking.service.AuthService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void it_should_sign_up_successfully() throws Exception {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .username("testuser")
                .password("password")
                .role(Role.PASSENGER)
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(request)));

        //then
        resultActions.andExpect(status().isCreated());
        ArgumentCaptor<SignUpRequest> argumentCaptor = ArgumentCaptor.forClass(SignUpRequest.class);
        verify(authService).signUp(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUsername()).isEqualTo("testuser");
    }


    @Test
    void it_should_sign_in_successfully() throws Exception {
        //given
        SignInRequest request = SignInRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        String token = "mocked-jwt-token";
        when(authService.signIn(any(SignInRequest.class))).thenReturn(token);

        //when
        ResultActions resultActions = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(request)));

        //then
        resultActions.andExpect(status().isOk());
        ArgumentCaptor<SignInRequest> argumentCaptor = ArgumentCaptor.forClass(SignInRequest.class);
        verify(authService).signIn(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUsername()).isEqualTo("testuser");
    }
}
