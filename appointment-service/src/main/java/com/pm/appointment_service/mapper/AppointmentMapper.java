package com.pm.appointment_service.mapper;

import com.pm.appointment_service.dto.AppointmentResponseDto;
import com.pm.appointment_service.model.Appointment;

public class AppointmentMapper {

    public static AppointmentResponseDto toResponseDto(Appointment appointment, String patientName) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatientId());
        dto.setPatientName(patientName);
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setReason(appointment.getReason());
        dto.setVersion(appointment.getVersion());
        return dto;
    }
}