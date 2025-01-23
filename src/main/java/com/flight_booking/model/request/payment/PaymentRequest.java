package com.flight_booking.model.request.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest implements Serializable {

    @NotNull
    @NotEmpty
    private List<String> seatNumbers;

    @NotNull
    @NotEmpty
    private String cardHolderName;

    @NotNull
    @NotEmpty
    private String cardNumber;

    @NotNull
    @NotEmpty
    private String cardExpireMonth;

    @NotNull
    @NotEmpty
    private String cardExpireYear;

    @NotNull
    @NotEmpty
    private String cardCvc;
}
