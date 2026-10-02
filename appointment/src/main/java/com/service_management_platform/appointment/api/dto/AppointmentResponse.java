package com.service_management_platform.appointment.api.dto;

import com.service_management_platform.appointment.domain.Appointment;
import com.service_management_platform.appointment.domain.AppointmentStatus;
import java.time.Instant;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID customerId,
        String description,
        Instant scheduledAt,
        AppointmentStatus status,
        Instant createdAt) {

    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(), appointment.getCustomerId(), appointment.getDescription(),
                appointment.getScheduledAt(), appointment.getStatus(), appointment.getCreatedAt());
    }
}
