package com.pm.appointment_service.kafka;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.pm.appointment_service.model.CachedPatient;
import com.pm.appointment_service.repository.CachedPatientRepository;

@Service
public class KafkaConsumer {

    private final CachedPatientRepository cachedPatientRepository;

    public KafkaConsumer(CachedPatientRepository cachedPatientRepository) {
        this.cachedPatientRepository = cachedPatientRepository;
    }

    @KafkaListener(topics = { "patient.created", "patient.updated" }, groupId = "appointment-group")
    public void consumeEvent(KafkaEvent event) {

        CachedPatient cachedPatient = new CachedPatient();
        cachedPatient.setId(UUID.fromString(event.getPatientId()));
        cachedPatient.setFullName(event.getName());
        cachedPatient.setEmail(event.getEmail());
        cachedPatient.setUpdatedAt(Instant.now());
        cachedPatientRepository.save(cachedPatient);
    }
}
