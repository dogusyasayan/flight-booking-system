package com.flight_booking.controller;

import com.flight_booking.model.request.payment.PaymentRequest;
import com.flight_booking.model.response.payment.PaymentResponse;
import com.flight_booking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{flightId}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PaymentResponse createPayment(@PathVariable Long flightId, @Valid @RequestBody PaymentRequest paymentRequest) {
        return paymentService.createPayment(flightId, paymentRequest);
    }
}
