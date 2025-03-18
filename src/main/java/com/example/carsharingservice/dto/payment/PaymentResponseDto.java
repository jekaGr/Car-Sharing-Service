package com.example.carsharingservice.dto.payment;

import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {
    private Long id;
    private String status;
    private String type;
    private Long rentalId;
    private URL sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}
