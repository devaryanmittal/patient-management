package com.pm.billing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BillingRequestDTO
 * 
 * REFACTORED: Removed redundant 'name' and 'email' fields
 * - These fields already exist in the Patient entity (patient-service owns this
 * data)
 * - Billing service should NOT duplicate this information
 * - Only stores patientId (foreign key) and accountType
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingRequestDTO {
    private String patientId;
    private String name;
    private String email;
    private String accountType;
}
