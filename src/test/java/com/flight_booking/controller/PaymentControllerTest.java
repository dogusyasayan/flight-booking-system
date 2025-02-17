package com.flight_booking.controller;

import com.flight_booking.model.request.payment.PaymentRequest;
import com.flight_booking.service.PaymentService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    @WithMockUser(username = "testUser", roles = "PASSENGER")
    void it_should_create_payment() throws Exception {
        // given
        String flightCode = "TK123";
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .seatNumbers(List.of("1A", "1B"))
                .cardHolderName("Name Username")
                .cardNumber("4111111111111111")
                .cardExpireMonth("12")
                .cardExpireYear("2030")
                .cardCvc("123")
                .build();

        // when
        mockMvc.perform(post("/payment/" + flightCode + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(paymentRequest)))
                .andExpect(status().isOk());

        // then
        ArgumentCaptor<String> argumentCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<PaymentRequest> argumentCaptor2 = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentService).createPayment(argumentCaptor1.capture(), argumentCaptor2.capture());

        String capturedFlightCode = argumentCaptor1.getValue();
        assertThat(capturedFlightCode).isEqualTo(flightCode);
    }

    @Test
    @WithMockUser(username = "testUser", roles = "PASSENGER")
    void it_should_calculate_total_amount() throws Exception {
        // given
        String flightCode = "TK123";
        List<String> seatNumbers = List.of("1A", "1B");
        BigDecimal expectedAmount = BigDecimal.valueOf(200.0);

        when(paymentService.calculateTotalPrice(any(), any())).thenReturn(expectedAmount);

        // when
        mockMvc.perform(post("/payment/calculate/" + flightCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(seatNumbers)))
                .andExpect(status().isOk());

        // then
        ArgumentCaptor<String> flightCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<String>> seatCaptor = ArgumentCaptor.forClass(List.class);
        verify(paymentService).calculateTotalPrice(flightCaptor.capture(), seatCaptor.capture());

        assertThat(flightCaptor.getValue()).isEqualTo(flightCode);
        assertThat(seatCaptor.getValue()).isEqualTo(seatNumbers);
    }
}
