package com.service_management_platform.appointment.application;

import java.util.UUID;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(UUID id) {
        super("Appointment with id '" + id + "' was not found");
    }
}
