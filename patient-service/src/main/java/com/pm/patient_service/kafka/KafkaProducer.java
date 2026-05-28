package com.pm.patient_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pm.patient_service.model.Patient;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, KafkaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPatientCreatedEvent(Patient patient) {
        try {
            KafkaEvent event = KafkaEvent.builder()
                    .patientId(patient.getId().toString())
                    .name(patient.getName())
                    .email(patient.getEmail())
                    .build();
            kafkaTemplate.send("patient.created", event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize/send event", e);
        }
    }

    public void sendPatientUpdatedEvent(Patient patient) {
        try {
            KafkaEvent event = KafkaEvent.builder()
                    .patientId(patient.getId().toString())
                    .name(patient.getName())
                    .email(patient.getEmail())
                    .build();
            kafkaTemplate.send("patient.updated", event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize/send event", e);
        }
    }
}
