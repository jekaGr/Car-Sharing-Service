package com.example.carsharingservice.service.payment;

import com.example.carsharingservice.dto.payment.PaymentRequestDto;
import com.example.carsharingservice.dto.payment.PaymentResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    public PaymentResponseDto createPayment(@Valid PaymentRequestDto requestDto);

    public Page<PaymentResponseDto> getAllPaymentsByUserId(Long userId, Pageable pageable);

    public void checkSuccessfulPayment(Long paymentId);
}
