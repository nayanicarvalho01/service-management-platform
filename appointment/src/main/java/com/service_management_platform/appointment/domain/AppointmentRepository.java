package com.service_management_platform.appointment.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findAllByCustomerIdOrderByScheduledAtDesc(UUID customerId);
}
