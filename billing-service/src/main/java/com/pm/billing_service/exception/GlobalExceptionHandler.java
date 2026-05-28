package com.pm.billing_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BillingAccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBillingAccountNotFound(
            BillingAccountNotFoundException ex, WebRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        errorMap.put("status", "NOT_FOUND");
        return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BillingAccountAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleBillingAccountAlreadyExists(
            BillingAccountAlreadyExistsException ex, WebRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        errorMap.put("status", "ALREADY_EXISTS");
        return new ResponseEntity<>(errorMap, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(
            Exception ex, WebRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "An error occurred: " + ex.getMessage());
        errorMap.put("status", "ERROR");
        return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
