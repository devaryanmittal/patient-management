package com.pm.notification_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final JavaMailSender mailSender;

    public KafkaConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "patient.created", groupId = "notification-group")
    public void handlePatientEvents(KafkaEvent event) {
        if ("PATIENT_CREATED".equalsIgnoreCase(event.getEventType())) {
            String email = event.getEmail();
            String name = event.getName();

            if (email != null) {
                sendEmail(email,
                        "Welcome to Our Healthcare Network!",
                        "Hello " + name + ",\n\nYour profile has been registered in the system successfully.");
            }
        }
    }

    @KafkaListener(topics = "appointment-event", groupId = "notification-group")
    public void handleAppointmentEvents(AppointmentEvent event) {
        if ("APPOINTMENT_CREATED".equalsIgnoreCase(event.getEventType())) {
            String email = event.getPatientEmail();
            String date = event.getAppointmentDate();

            if (email != null) {
                sendEmail(email,
                        "Appointment Scheduled Successfully",
                        "Hello,\n\nYour medical appointment has been recorded for: " + date);
            }
        }
    }

    private void sendEmail(String toEmail, String subject, String messageText) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@healthcareapp.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(messageText);
            mailSender.send(message);
            System.out.println("Dispatched notification message successfully to: " + toEmail);
        } catch (MailException e) {
            System.err.println("Failed to transmit email notification to " + toEmail + ": " + e.getMessage());
        }
    }
}