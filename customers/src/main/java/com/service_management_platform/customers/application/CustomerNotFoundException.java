package com.service_management_platform.customers.application;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(UUID id) {
        super("Customer with id '" + id + "' was not found");
    }

    public CustomerNotFoundException(String lookup) {
        super("Customer with " + lookup + " was not found");
    }
}