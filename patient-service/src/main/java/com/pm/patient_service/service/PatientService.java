package com.pm.patient_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
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

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingInterface billingInterface;
    private final KafkaProducer kafkaProducer;

    private PatientService self;

    public PatientService(PatientRepository patientRepository, BillingInterface billingInterface,
            KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingInterface = billingInterface;
        this.kafkaProducer = kafkaProducer;
    }

    @Autowired
    public void setSelf(@Lazy PatientService self) {
        this.self = self;
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
        // Use self-invocation to ensure Spring AOP intercepts the circuit breaker
        self.callBillingService(savedPatient);
        kafkaProducer.sendPatientCreatedEvent(savedPatient);
        return PatientMapper.toDTO(savedPatient);
    }

    @CircuitBreaker(name = "billingService", fallbackMethod = "callBillingServiceFallback")
    @Retry(name = "billingRetry")
    public void callBillingService(Patient savedPatient) {
        BillingRequestDTO billingRequest = new BillingRequestDTO();
        // Use Feign client to create billing account for the new patient
        billingRequest.setName(savedPatient.getName());
        billingRequest.setEmail(savedPatient.getEmail());
        billingRequest.setPatientId(savedPatient.getId().toString());
        billingRequest.setAccountType("STANDARD");
        billingInterface.createBillingAccount(billingRequest);
    }

    public void callBillingServiceFallback(Patient savedPatient, Throwable e) {
        // Log error but don't fail patient creation if billing service fails or circuit
        // is open
        System.err
                .println("Failed to create billing account for patient (Circuit Breaker Fallback): " + e.getMessage());
        kafkaProducer.sendBillingAccountEvent(savedPatient);
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
