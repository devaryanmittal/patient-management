package com.pm.analytics_service.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardMetrics {

    private Long id;
    private String patientId;
    private String eventType;
    private LocalDateTime eventTimestamp;

}