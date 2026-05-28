package com.pm.patient_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pm.patient_service.client.BillingInterface;
import com.pm.patient_service.dto.BillingRequestDTO;
import com.pm.patient_service.dto.PagedPatientResponseDTO;
import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exception.EmailAlreadyExistsException;
import com.pm.patient_service.exception.PatientNotFoundException;
import com.pm.patient_service.kafka.KafkaProducer;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingInterface patientInterface;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingInterface patientInterface,
            KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.patientInterface = patientInterface;
        this.kafkaProducer = kafkaProducer;
    }

    @Cacheable(value = "patients", key = "{#size, #page, #sort, #sortField}", condition = "#searchValue == null || #searchValue.isEmpty()")
    public PagedPatientResponseDTO getPatients(int size, int page, String sort, String sortField, String searchValue) {

        Pageable pageable = PageRequest.of(page - 1, size,
                sort.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending());

        Page<Patient> patientPage;
        if (searchValue != null && !searchValue.isEmpty()) {
            patientPage = patientRepository.findByNameContainingIgnoreCase(searchValue, pageable);
        } else {
            patientPage = patientRepository.findAll(pageable);
        }

        List<PatientResponseDTO> patientResponseDTOs = patientPage.getContent()
                .stream()
                .map(patient -> PatientMapper.toDTO(patient))
                .toList();

        PagedPatientResponseDTO pagedPatientResponseDTO = new PagedPatientResponseDTO();
        pagedPatientResponseDTO.setPatients(patientResponseDTOs);
        pagedPatientResponseDTO.setPage(patientPage.getNumber() + 1);
        pagedPatientResponseDTO.setSize(patientPage.getSize());
        pagedPatientResponseDTO.setTotalElements((int) patientPage.getTotalElements());
        pagedPatientResponseDTO.setTotalPages(patientPage.getTotalPages());

        return pagedPatientResponseDTO;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + patientRequestDTO.getEmail());
        }
        Patient patient = PatientMapper.toPatient(patientRequestDTO);
        Patient savedPatient = patientRepository.save(patient);

        // Use Feign client to create billing account for the new patient
        try {
            BillingRequestDTO billingRequest = new BillingRequestDTO();
            billingRequest.setName(savedPatient.getName());
            billingRequest.setEmail(savedPatient.getEmail());
            billingRequest.setPatientId(savedPatient.getId().toString());
            billingRequest.setAccountType("STANDARD");
            patientInterface.createBillingAccount(billingRequest);
        } catch (Exception e) {
            // Log error but don't fail patient creation if billing service fails
            System.err.println("Failed to create billing account for patient: " + e.getMessage());
        }
        kafkaProducer.sendPatientCreatedEvent(savedPatient);
        return PatientMapper.toDTO(savedPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email already exists: " + patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        Patient updatedPatient = patientRepository.save(patient);
        kafkaProducer.sendPatientUpdatedEvent(updatedPatient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}
