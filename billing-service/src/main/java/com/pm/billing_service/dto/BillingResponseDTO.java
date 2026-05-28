package com.pm.billing_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BillingResponseDTO
 * 
 * REFACTORED: Removed redundant 'name' and 'email' fields
 * - These fields already exist in the Patient entity (patient-service owns this
 * data)
 * - Billing service should NOT duplicate this information
 * - Response contains only billing-specific information
 * - Client can call patient-service separately to get patient name/email if
 * needed
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingResponseDTO {

    private String id;

    private String patientId;

    private String name;

    private String email;

    private String status;

    private String accountType;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
