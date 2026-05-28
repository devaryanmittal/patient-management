package com.pm.billing_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pm.billing_service.model.BillingAccount;

@Repository
public interface BillingAccountRepository extends JpaRepository<BillingAccount, UUID> {

    Optional<BillingAccount> findByPatientId(UUID patientId);

    boolean existsByPatientId(UUID patientId);

    boolean existsByEmail(String email);
}
