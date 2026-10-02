package com.service_management_platform.appointment.api;

import com.service_management_platform.appointment.api.dto.AppointmentResponse;
import com.service_management_platform.appointment.api.dto.CreateAppointmentRequest;
import com.service_management_platform.appointment.application.AppointmentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentResponse response = AppointmentResponse.from(service.create(request));
        return ResponseEntity.created(URI.create("/api/appointments/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public AppointmentResponse getById(@PathVariable UUID id) {
        return AppointmentResponse.from(service.getById(id));
    }

    @GetMapping(params = "customerId")
    public List<AppointmentResponse> getByCustomerId(@RequestParam UUID customerId) {
        return service.getByCustomerId(customerId).stream()
                .map(AppointmentResponse::from)
                .toList();
    }
}
