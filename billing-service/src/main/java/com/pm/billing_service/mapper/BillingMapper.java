package com.pm.billing_service.mapper;

import java.util.UUID;

import com.pm.billing_service.dto.BillingRequestDTO;
import com.pm.billing_service.dto.BillingResponseDTO;
import com.pm.billing_service.model.BillingAccount;

/**
 * BillingMapper
 * Maps between BillingAccount entity and DTOs
 * 
 * REFACTORED: Removed name and email mapping
 * - These fields are no longer stored in BillingAccount (avoid data
 * duplication)
 * - They exist in Patient entity which is owned by patient-service
 */
public class BillingMapper {

    public static BillingAccount toEntity(BillingRequestDTO requestDTO) {
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setPatientId(UUID.fromString(requestDTO.getPatientId()));
        billingAccount.setName(requestDTO.getName());
        billingAccount.setEmail(requestDTO.getEmail());
        billingAccount.setAccountType(requestDTO.getAccountType() != null ? requestDTO.getAccountType() : "STANDARD");
        billingAccount.setStatus("ACTIVE");
        return billingAccount;
    }

    public static BillingResponseDTO toDTO(BillingAccount billingAccount) {
        BillingResponseDTO responseDTO = new BillingResponseDTO();
        responseDTO.setId(billingAccount.getId().toString());
        responseDTO.setStatus(billingAccount.getStatus());
        responseDTO.setPatientId(billingAccount.getPatientId().toString());
        responseDTO.setName(billingAccount.getName());
        responseDTO.setEmail(billingAccount.getEmail());
        responseDTO.setAccountType(billingAccount.getAccountType());
        responseDTO.setCreatedDate(billingAccount.getCreatedDate());
        responseDTO.setLastModifiedDate(billingAccount.getLastModifiedDate());
        return responseDTO;
    }
}
