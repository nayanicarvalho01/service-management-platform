package com.service_management_platform.customers.api;

import com.service_management_platform.customers.api.dto.CreateCustomerRequest;
import com.service_management_platform.customers.api.dto.CustomerResponse;
import com.service_management_platform.customers.application.CustomerService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = CustomerResponse.from(service.create(request));
        return ResponseEntity.created(URI.create("/api/customers/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable UUID id) {
        return CustomerResponse.from(service.getById(id));
    }

    @GetMapping(params = "email")
    public CustomerResponse getByEmail(@RequestParam String email) {
        return CustomerResponse.from(service.getByEmail(email));
    }
}
