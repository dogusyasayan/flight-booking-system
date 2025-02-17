package com.flight_booking.controller;

import com.flight_booking.model.request.payment.PaymentRequest;
import com.flight_booking.model.response.payment.PaymentResponse;
import com.flight_booking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/calculate/{flightCode}")
    @PreAuthorize("hasRole('PASSENGER')")
    public BigDecimal calculateTotalAmount(@PathVariable String flightCode, @RequestBody List<String> seatNumbers) {
        return paymentService.calculateTotalPrice(flightCode, seatNumbers);
    }

    @PostMapping("/{flightCode}/")
    @PreAuthorize("hasRole('PASSENGER')")
    public PaymentResponse createPayment(@PathVariable String flightCode, @Valid @RequestBody PaymentRequest paymentRequest) {
        return paymentService.createPayment(flightCode, paymentRequest);
    }
}
