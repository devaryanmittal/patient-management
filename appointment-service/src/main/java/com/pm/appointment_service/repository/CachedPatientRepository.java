package com.pm.appointment_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pm.appointment_service.model.CachedPatient;

@Repository
public interface CachedPatientRepository extends JpaRepository<CachedPatient, UUID> {

}
