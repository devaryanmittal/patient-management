package com.pm.chatbot_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pm.chatbot_service.dto.BillingResponseDto;

@FeignClient(name = "billing-service")
public interface BillingClient {
    @GetMapping("/api/billing/patient/{patientId}")
    BillingResponseDto getBillingByPatientId(@PathVariable("patientId") String patientId);
}