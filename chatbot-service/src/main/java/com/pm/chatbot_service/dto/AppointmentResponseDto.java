package com.pm.chatbot_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {

    private UUID id;
    private UUID patientId;
    private String patientName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
    private Long version;

}
