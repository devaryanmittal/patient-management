package com.pm.analytics_service.service;

import java.time.LocalDateTime;

import com.pm.analytics_service.kafka.KafkaEvent;
import com.pm.analytics_service.model.DashboardMetrics;
import com.pm.analytics_service.repository.DashboardMetricsRepository;

public class AnalyticsService {

    private final DashboardMetricsRepository metricsRepository;

    public AnalyticsService(DashboardMetricsRepository metricsRepository) {
        this.metricsRepository = metricsRepository;
    }

    public void processPatientEvent(KafkaEvent event) {

        DashboardMetrics metricEvent = DashboardMetrics.builder()
                .patientId(event.getPatientId())
                .eventType(event.getEventType())
                .eventTimestamp(LocalDateTime.now())
                .build();

        metricsRepository.save(metricEvent);
    }
    // Optional: If you also want to keep a running total in a separate table,
    // you would manage that logic here as well.
}