package com.pm.billing_service.exception;

public class BillingAccountAlreadyExistsException extends RuntimeException {

    public BillingAccountAlreadyExistsException(String message) {
        super(message);
    }

    public BillingAccountAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
