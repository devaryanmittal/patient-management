package com.pm.chatbot_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pm.chatbot_service.dto.AppointmentResponseDto;

@FeignClient(name = "appointment-service")
public interface AppointmentClient {
    @GetMapping("/api/appointments/patient/{patientId}")
    List<AppointmentResponseDto> getAppointmentsByPatientId(@PathVariable("patientId") String patientId);
}