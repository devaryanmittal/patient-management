package com.pm.analytics_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.pm.analytics_service.service.AnalyticsService;

@Service
public class KafkaConsumer {

    private final AnalyticsService analyticsService;

    public KafkaConsumer(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @KafkaListener(topics = "patient.created", groupId = "analytics-group")
    public void consumePatientCreated(KafkaEvent event) {
        event.setEventType("CREATED"); // Ensure event type is set
        analyticsService.processPatientEvent(event);
    }

    @KafkaListener(topics = "patient.updated", groupId = "analytics-group")
    public void consumePatientUpdated(KafkaEvent event) {
        event.setEventType("UPDATED");
        analyticsService.processPatientEvent(event);
    }
}
