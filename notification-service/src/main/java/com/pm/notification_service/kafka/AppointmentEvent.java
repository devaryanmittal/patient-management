package com.pm.notification_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEvent {
    private String appointmentId;
    private String patientId;
    private String patientName;
    private String patientEmail;
    private String appointmentDate;
    private String eventType;
}