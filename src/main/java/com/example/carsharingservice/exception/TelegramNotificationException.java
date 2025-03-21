package com.example.carsharingservice.exception;

public class TelegramNotificationException extends RuntimeException {
    public TelegramNotificationException(String string, Throwable throwable) {
        super(string,throwable);
    }
}
