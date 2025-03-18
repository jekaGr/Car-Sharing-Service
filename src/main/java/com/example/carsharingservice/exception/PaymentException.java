package com.example.carsharingservice.exception;

public class PaymentException extends RuntimeException {
    public PaymentException(String string) {
        super(string);
    }
}
