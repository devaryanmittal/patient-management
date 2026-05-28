package com.pm.patient_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaEvent {

    private String patientId;
    private String name;
    private String email;
    private String eventType;
}