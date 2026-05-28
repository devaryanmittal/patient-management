package com.pm.appointment_service.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CachedPatient {

    @Id
    private UUID id;
    private String fullName;
    private String email;
    private Instant updatedAt;

}
