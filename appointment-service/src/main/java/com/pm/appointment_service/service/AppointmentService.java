package com.pm.appointment_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pm.appointment_service.dto.AppointmentResponseDto;
import com.pm.appointment_service.repository.AppointmentRepository;
import com.pm.appointment_service.repository.CachedPatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CachedPatientRepository cachedPatientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            CachedPatientRepository cachedPatientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.cachedPatientRepository = cachedPatientRepository;
    }

    public List<AppointmentResponseDto> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {

        return appointmentRepository.findByStartTimeBetween(startDate, endDate)
                .stream()
                .map(appointment -> {
                    String name = cachedPatientRepository.findById(appointment.getPatientId())
                            .map(cachedPatient -> cachedPatient.getFullName())
                            .orElse("Unknown");
                    AppointmentResponseDto appointmentResponseDto = new AppointmentResponseDto();
                    appointmentResponseDto.setId(appointment.getId());
                    appointmentResponseDto.setPatientId(appointment.getPatientId());
                    appointmentResponseDto.setPatientName(name);
                    appointmentResponseDto.setStartTime(appointment.getStartTime());
                    appointmentResponseDto.setEndTime(appointment.getEndTime());
                    appointmentResponseDto.setReason(appointment.getReason());
                    appointmentResponseDto.setVersion(appointment.getVersion());
                    return appointmentResponseDto;
                })
                .toList();
    }
}
