package com.pm.patient_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingRequestDTO {

    private String patientId;

    private String name;

    private String email;

    private String accountType;
}
