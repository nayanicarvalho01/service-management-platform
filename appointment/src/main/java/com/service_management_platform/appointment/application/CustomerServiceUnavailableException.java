package com.service_management_platform.appointment.application;

public class CustomerServiceUnavailableException extends RuntimeException {
    public CustomerServiceUnavailableException(Throwable cause) {
        super("Customer Service is unavailable", cause);
    }
}
