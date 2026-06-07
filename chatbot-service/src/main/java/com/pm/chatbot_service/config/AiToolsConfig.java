package com.pm.chatbot_service.config;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import com.pm.chatbot_service.client.AppointmentClient;
import com.pm.chatbot_service.client.BillingClient;
import com.pm.chatbot_service.dto.AppointmentResponseDto;
import com.pm.chatbot_service.dto.BillingResponseDto;

@Service
public class AiToolsConfig {

    private final BillingClient billingClient;
    private final AppointmentClient appointmentClient;

    // Normal Constructor Injection
    public AiToolsConfig(BillingClient billingClient, AppointmentClient appointmentClient) {
        this.billingClient = billingClient;
        this.appointmentClient = appointmentClient;
    }

    // ==========================================
    // TOOL 1: Billing
    // ==========================================
    @Tool(description = "Fetch the billing account details, outstanding amount, and payment status for a specific patient ID.")
    public BillingResponseDto getPatientBilling(
            @ToolParam(description = "The unique patient ID to search for") String patientId) {

        // Look how simple this is! No "request.patientId()" needed.
        return billingClient.getBillingByPatientId(patientId);
    }

    // ==========================================
    // TOOL 2: Appointments
    // ==========================================
    @Tool(description = "Fetch all scheduled medical appointments and their statuses for a specific patient ID.")
    public List<AppointmentResponseDto> getPatientAppointments(
            @ToolParam(description = "The unique patient ID to search for") String patientId) {

        return appointmentClient.getAppointmentsByPatientId(patientId);
    }
}