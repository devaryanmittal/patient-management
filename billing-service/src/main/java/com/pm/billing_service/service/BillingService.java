package com.pm.billing_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pm.billing_service.dto.BillingRequestDTO;
import com.pm.billing_service.dto.BillingResponseDTO;
import com.pm.billing_service.exception.BillingAccountAlreadyExistsException;
import com.pm.billing_service.exception.BillingAccountNotFoundException;
import com.pm.billing_service.mapper.BillingMapper;
import com.pm.billing_service.model.BillingAccount;
import com.pm.billing_service.repository.BillingAccountRepository;

@Service
public class BillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingService.class);

    private final BillingAccountRepository billingAccountRepository;

    public BillingService(BillingAccountRepository billingAccountRepository) {
        this.billingAccountRepository = billingAccountRepository;
    }

    public List<BillingResponseDTO> getAllBillingAccounts() {
        List<BillingAccount> billingAccounts = billingAccountRepository.findAll();
        return billingAccounts.stream()
                .map(BillingMapper::toDTO)
                .toList();
    }

    public BillingResponseDTO getBillingAccountByPatientId(UUID patientId) {
        BillingAccount billingAccount = billingAccountRepository.findByPatientId(patientId)
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found for patient: " + patientId));
        return BillingMapper.toDTO(billingAccount);
    }

    public BillingResponseDTO createBillingAccount(BillingRequestDTO billingRequestDTO) {
        if (billingAccountRepository.existsByPatientId(UUID.fromString(billingRequestDTO.getPatientId()))) {
            throw new BillingAccountAlreadyExistsException(
                    "Billing account already exists for patient: " + billingRequestDTO.getPatientId());
        }

        log.info("Creating billing account for patient: {}", billingRequestDTO.getPatientId());

        BillingAccount billingAccount = BillingMapper.toEntity(billingRequestDTO);
        billingAccount.setCreatedDate(LocalDateTime.now());
        billingAccount.setLastModifiedDate(LocalDateTime.now());

        BillingAccount savedAccount = billingAccountRepository.save(billingAccount);
        log.info("Billing account created successfully with ID: {}", savedAccount.getId());

        return BillingMapper.toDTO(savedAccount);
    }

    public BillingResponseDTO updateBillingAccount(UUID id, BillingRequestDTO billingRequestDTO) {
        BillingAccount billingAccount = billingAccountRepository.findById(id)
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found with id: " + id));

        // Only update accountType (patientId and other fields are immutable)
        // Name and email are NOT stored in BillingAccount anymore - they belong to
        // Patient entity
        if (billingRequestDTO.getAccountType() != null) {
            billingAccount.setAccountType(billingRequestDTO.getAccountType());
        }
        billingAccount.setLastModifiedDate(LocalDateTime.now());

        BillingAccount updatedAccount = billingAccountRepository.save(billingAccount);
        return BillingMapper.toDTO(updatedAccount);
    }

    public void deleteBillingAccount(UUID id) {
        if (!billingAccountRepository.existsById(id)) {
            throw new BillingAccountNotFoundException("Billing account not found with id: " + id);
        }
        billingAccountRepository.deleteById(id);
        log.info("Billing account deleted with ID: {}", id);
    }

    public void deleteBillingAccountByPatientId(UUID patientId) {
        BillingAccount billingAccount = billingAccountRepository.findByPatientId(patientId)
                .orElseThrow(() -> new BillingAccountNotFoundException(
                        "Billing account not found for patient: " + patientId));
        billingAccountRepository.delete(billingAccount);
        log.info("Billing account deleted for patient: {}", patientId);
    }
}
