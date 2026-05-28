package com.pm.patient_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.pm.patient_service.dto.BillingRequestDTO;
import com.pm.patient_service.dto.BillingResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@FeignClient(name = "billing-service")
public interface BillingInterface {

    @PostMapping("/billing-accounts")
    @Operation(summary = "Create a new billing account", description = "Create a new billing account for a patient")
    public ResponseEntity<BillingResponseDTO> createBillingAccount(
            @Valid @RequestBody BillingRequestDTO billingRequestDTO);
}
