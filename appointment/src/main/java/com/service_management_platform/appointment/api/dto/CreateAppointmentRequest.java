package com.service_management_platform.appointment.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public record CreateAppointmentRequest(
        @NotNull UUID customerId,
        @NotBlank @Size(max = 300) String description,
        @NotNull @Future Instant scheduledAt) {
}
