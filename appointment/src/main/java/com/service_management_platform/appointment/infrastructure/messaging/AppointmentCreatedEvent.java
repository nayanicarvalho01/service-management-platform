package com.service_management_platform.appointment.infrastructure.messaging;

import com.service_management_platform.appointment.domain.Appointment;

public record AppointmentCreatedEvent(
        String appointmentId,
        String customerId,
        String description,
        String scheduledAt,
        String status,
        String createdAt) {

    public static AppointmentCreatedEvent from(Appointment appointment) {
        return new AppointmentCreatedEvent(
                appointment.getId().toString(),
                appointment.getCustomerId().toString(),
                appointment.getDescription(),
                appointment.getScheduledAt().toString(),
                appointment.getStatus().name(),
                appointment.getCreatedAt().toString());
    }
}