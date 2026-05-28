package com.pm.patient_service.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pm.patient_service.dto.PagedPatientResponseDTO;
import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.dto.validators.CreatePatientValidationGroup;
import com.pm.patient_service.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "APIs for managing patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieve a list of all patients")
    public ResponseEntity<PagedPatientResponseDTO> getPatients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "") String searchValue) {
        PagedPatientResponseDTO patients = patientService.getPatients(page, size, sort, sortField, searchValue);
        return ResponseEntity.ok().body(patients);
    }

    @Operation(summary = "Create a new patient", description = "Create a new patient with the provided details")
    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({ Default.class,
            CreatePatientValidationGroup.class }) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @Operation(summary = "Update an existing patient", description = "Update the details of an existing patient by ID")
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id,
            @Validated({ Default.class }) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @Operation(summary = "Delete a patient", description = "Delete an existing patient by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok().body("Patient deleted successfully :" + id);
    }
}
