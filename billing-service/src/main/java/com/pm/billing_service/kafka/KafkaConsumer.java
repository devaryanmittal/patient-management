package com.pm.billing_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.pm.billing_service.dto.BillingRequestDTO;
import com.pm.billing_service.service.BillingService;

@Service
public class KafkaConsumer {

    private final BillingService billingService;

    public KafkaConsumer(BillingService billingService) {
        this.billingService = billingService;
    }

    @KafkaListener(topics = "billing.account.pending", groupId = "billing-group")
    public void consumePendingBillingAccount(KafkaEvent event) {
        try {
            BillingRequestDTO request = new BillingRequestDTO();
            request.setPatientId(event.getPatientId());
            request.setName(event.getName());
            request.setEmail(event.getEmail());
            request.setAccountType("STANDARD");
            billingService.createBillingAccount(request);
        } catch (Exception e) {
            System.err.println("Failed to process pending billing account for patient {}: {} :" + event.getPatientId() +
                    " " + e.getMessage());
            // You can implement dead-letter queue (DLQ) logic here if needed
        }
    }
}
