package com.service_management_platform.appointment.application;

import com.service_management_platform.appointment.api.dto.CreateAppointmentRequest;
import com.service_management_platform.appointment.domain.Appointment;
import com.service_management_platform.appointment.domain.AppointmentRepository;
import com.service_management_platform.appointment.infrastructure.customer.CustomerClient;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final CustomerClient customerClient;

    public AppointmentService(AppointmentRepository appointments, CustomerClient customerClient) {
        this.appointments = appointments;
        this.customerClient = customerClient;
    }

    public Appointment create(CreateAppointmentRequest request) {
        customerClient.ensureCustomerExists(request.customerId());
        Appointment appointment = new Appointment(
                request.customerId(), request.description().trim(), request.scheduledAt());
        return appointments.save(appointment);
    }

    public Appointment getById(UUID id) {
        return appointments.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    public List<Appointment> getByCustomerId(UUID customerId) {
        return appointments.findAllByCustomerIdOrderByScheduledAtDesc(customerId);
    }
}
