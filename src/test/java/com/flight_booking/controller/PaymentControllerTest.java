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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.verify;
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
    void it_should_get_create_payment() throws Exception {
        //given
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .seatNumbers(List.of("1A", "1B"))
                .cardHolderName("Name Username")
                .cardNumber("4111111111111111")
                .cardExpireMonth("12")
                .cardExpireYear("2030")
                .cardCvc("123")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(post("/payment/123/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(paymentRequest))
        );

        //then
        resultActions.andExpect(status().isAccepted());
        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<PaymentRequest> argumentCaptor2 = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentService).createPayment(argumentCaptor1.capture(), argumentCaptor2.capture());
    }
}