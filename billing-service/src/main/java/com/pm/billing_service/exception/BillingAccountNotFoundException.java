package com.pm.billing_service.exception;

public class BillingAccountNotFoundException extends RuntimeException {

    public BillingAccountNotFoundException(String message) {
        super(message);
    }

    public BillingAccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
