package com.service_management_platform.customers.application;

public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException() {
        super("A customer with this email already exists");
    }
}