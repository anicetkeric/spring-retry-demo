package com.bootlabs.demo.exception;

public class BankApiException extends RuntimeException {
    public BankApiException(String message) {
        super(message);
    }
}