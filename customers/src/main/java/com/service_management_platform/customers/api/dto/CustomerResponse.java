package com.service_management_platform.customers.api.dto;

import com.service_management_platform.customers.domain.Customer;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(UUID id, String name, String email, String phone, Instant createdAt) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(), customer.getName(), customer.getEmail(),
                customer.getPhone(), customer.getCreatedAt());
    }
}
