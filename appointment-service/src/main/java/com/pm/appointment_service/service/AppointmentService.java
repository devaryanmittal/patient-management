package com.pm.appointment_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pm.appointment_service.dto.AppointmentRequestDto;
import com.pm.appointment_service.dto.AppointmentResponseDto;
import com.pm.appointment_service.kafka.AppointmentEvent;
import com.pm.appointment_service.mapper.AppointmentMapper;
import com.pm.appointment_service.model.Appointment;
import com.pm.appointment_service.model.CachedPatient;
import com.pm.appointment_service.repository.AppointmentRepository;
import com.pm.appointment_service.repository.CachedPatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CachedPatientRepository cachedPatientRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AppointmentService(AppointmentRepository appointmentRepository,
            CachedPatientRepository cachedPatientRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.cachedPatientRepository = cachedPatientRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<AppointmentResponseDto> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {

        return appointmentRepository.findByStartTimeBetween(startDate, endDate)
                .stream()
                .map(appointment -> {
                    String name = cachedPatientRepository.findById(appointment.getPatientId())
                            .map(cachedPatient -> cachedPatient.getFullName())
                            .orElse("Unknown");
                    return AppointmentMapper.toResponseDto(appointment, name);
                })
                .toList();
    }

    public AppointmentResponseDto getAppointmentByPatientId(java.util.UUID patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .map(appointment -> {
                    String name = cachedPatientRepository.findById(appointment.getPatientId())
                            .map(cachedPatient -> cachedPatient.getFullName())
                            .orElse("Unknown");
                    return AppointmentMapper.toResponseDto(appointment, name);
                }).orElseThrow(() -> new RuntimeException("Appointment not found for patient id: " + patientId));
    }

    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        Optional<Appointment> existingAppointment = appointmentRepository
                .findByPatientId(requestDto.getPatientId());

        Appointment appointment;
        if (existingAppointment.isPresent()) {
            appointment = existingAppointment.get();
            appointment.setStartTime(requestDto.getStartTime());
            appointment.setEndTime(requestDto.getEndTime());
            appointment.setReason(requestDto.getReason());
        } else {
            appointment = new Appointment();
            appointment.setPatientId(requestDto.getPatientId());
            appointment.setStartTime(requestDto.getStartTime());
            appointment.setEndTime(requestDto.getEndTime());
            appointment.setReason(requestDto.getReason());
        }
        Appointment savedAppointment = appointmentRepository.save(appointment);

        Optional<CachedPatient> cachedPatientOpt = cachedPatientRepository.findById(savedAppointment.getPatientId());
        String patientName = cachedPatientOpt.map(CachedPatient::getFullName).orElse("Unknown");
        String patientEmail = cachedPatientOpt.map(CachedPatient::getEmail).orElse(null);

        // Publish event to Kafka for the Notification Service
        AppointmentEvent event = AppointmentEvent.builder()
                .appointmentId(savedAppointment.getId().toString())
                .patientId(savedAppointment.getPatientId().toString())
                .patientName(patientName)
                .patientEmail(patientEmail)
                .appointmentDate(savedAppointment.getStartTime().toString())
                .eventType("APPOINTMENT_CREATED")
                .build();
        kafkaTemplate.send("appointment-event", event);
        return AppointmentMapper.toResponseDto(savedAppointment, patientName);
    }
}
