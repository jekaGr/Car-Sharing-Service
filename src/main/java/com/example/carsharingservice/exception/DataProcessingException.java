package com.example.carsharingservice.exception;

public class DataProcessingException extends RuntimeException {
    public DataProcessingException(String string, Exception e) {
        super(string, e);
    }
}
