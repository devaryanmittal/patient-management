package com.pm.analytics_service.kafka;

import org.springframework.stereotype.Service;

import com.pm.analytics_service.service.AnalyticsService;

@Service
public class KafkaConsumer {

    private final AnalyticsService analyticsService;

    public KafkaConsumer(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    public void consumePatientCreated(KafkaEvent event) {
        event.setEventType("CREATED"); // Ensure event type is set
        analyticsService.processPatientEvent(event);
    }

    public void consumePatientUpdated(KafkaEvent event) {
        event.setEventType("UPDATED");
        analyticsService.processPatientEvent(event);
    }
}
