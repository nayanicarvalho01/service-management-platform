package com.service_management_platform.customers.application;

import java.util.Locale;
import java.util.UUID;

import com.service_management_platform.customers.api.dto.CreateCustomerRequest;
import com.service_management_platform.customers.domain.Customer;
import com.service_management_platform.customers.domain.CustomerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) {
        this.customers = customers;
    }

    @Transactional
    public Customer create(CreateCustomerRequest request) {
        String email = normalizeEmail(request.email());
        if (customers.existsByEmailIgnoreCase(email)) {
            throw new DuplicateCustomerException();
        }

        try {
            return customers.saveAndFlush(new Customer(
                    request.name().trim(), email, normalizePhone(request.phone())));
        } catch (DataIntegrityViolationException exception) {
            // Protects against two concurrent requests passing the pre-check.
            throw new DuplicateCustomerException();
        }
    }

    public Customer getById(UUID id) {
        return customers.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer getByEmail(String email) {
        String normalized = normalizeEmail(email);
        return customers.findByEmailIgnoreCase(normalized)
                .orElseThrow(() -> new CustomerNotFoundException("the supplied email"));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        return phone.trim();
    }
}
