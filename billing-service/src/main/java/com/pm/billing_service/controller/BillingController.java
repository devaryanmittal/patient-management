package com.pm.billing_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.billing_service.dto.BillingRequestDTO;
import com.pm.billing_service.dto.BillingResponseDTO;
import com.pm.billing_service.service.BillingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/billing-accounts")
@Validated
@Tag(name = "Billing", description = "APIs for managing billing accounts")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    @Operation(summary = "Get all billing accounts", description = "Retrieve a list of all billing accounts")
    public ResponseEntity<List<BillingResponseDTO>> getAllBillingAccounts() {
        List<BillingResponseDTO> billingAccounts = billingService.getAllBillingAccounts();
        return ResponseEntity.ok().body(billingAccounts);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get billing account by patient ID", description = "Retrieve a billing account for a specific patient")
    public ResponseEntity<BillingResponseDTO> getBillingAccountByPatientId(@PathVariable UUID patientId) {
        BillingResponseDTO billingAccount = billingService.getBillingAccountByPatientId(patientId);
        return ResponseEntity.ok().body(billingAccount);
    }

    @PostMapping
    @Operation(summary = "Create a new billing account", description = "Create a new billing account for a patient")
    public ResponseEntity<BillingResponseDTO> createBillingAccount(
            @Valid @RequestBody BillingRequestDTO billingRequestDTO) {
        BillingResponseDTO billingAccount = billingService.createBillingAccount(billingRequestDTO);
        return ResponseEntity.ok().body(billingAccount);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a billing account", description = "Update the details of an existing billing account")
    public ResponseEntity<BillingResponseDTO> updateBillingAccount(@PathVariable UUID id,
            @Valid @RequestBody BillingRequestDTO billingRequestDTO) {
        BillingResponseDTO billingAccount = billingService.updateBillingAccount(id, billingRequestDTO);
        return ResponseEntity.ok().body(billingAccount);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a billing account by ID", description = "Delete an existing billing account by its ID")
    public ResponseEntity<String> deleteBillingAccount(@PathVariable UUID id) {
        billingService.deleteBillingAccount(id);
        return ResponseEntity.ok().body("Billing account deleted successfully: " + id);
    }

    @DeleteMapping("/patient/{patientId}")
    @Operation(summary = "Delete billing account by patient ID", description = "Delete the billing account associated with a patient")
    public ResponseEntity<String> deleteBillingAccountByPatientId(@PathVariable UUID patientId) {
        billingService.deleteBillingAccountByPatientId(patientId);
        return ResponseEntity.ok().body("Billing account deleted successfully for patient: " + patientId);
    }
}
