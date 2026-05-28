package com.pm.patient_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedPatientResponseDTO {
    private List<PatientResponseDTO> patients;
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;
}
