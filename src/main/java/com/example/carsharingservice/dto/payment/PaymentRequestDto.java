package com.example.carsharingservice.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class PaymentRequestDto {
    @Positive
    @NotNull
    private Long rentalId;
}
