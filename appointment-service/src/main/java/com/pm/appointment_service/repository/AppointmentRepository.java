package com.pm.appointment_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pm.appointment_service.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

}
