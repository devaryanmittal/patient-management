package com.pm.patient_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
