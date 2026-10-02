package com.service_management_platform.appointment.api;

import com.service_management_platform.appointment.application.AppointmentNotFoundException;
import com.service_management_platform.appointment.application.CustomerNotFoundException;
import com.service_management_platform.appointment.application.CustomerServiceUnavailableException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AppointmentNotFoundException.class)
    ProblemDetail handleAppointmentNotFound(AppointmentNotFoundException exception, WebRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Appointment not found", exception.getMessage(), request);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    ProblemDetail handleCustomerNotFound(CustomerNotFoundException exception, WebRequest request) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Customer not found", exception.getMessage(), request);
    }

    @ExceptionHandler(CustomerServiceUnavailableException.class)
    ProblemDetail handleCustomerServiceUnavailable(
            CustomerServiceUnavailableException exception, WebRequest request) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "Customer Service unavailable",
                exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception, WebRequest request) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst().orElse("Request validation failed");
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", detail, request);
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://example.com/problems/" + status.value()));
        problem.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return problem;
    }
}
