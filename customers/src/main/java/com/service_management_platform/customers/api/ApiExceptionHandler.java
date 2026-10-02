package com.service_management_platform.customers.api;

import java.net.URI;

import com.service_management_platform.customers.application.CustomerNotFoundException;
import com.service_management_platform.customers.application.DuplicateCustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(CustomerNotFoundException.class)
    ProblemDetail handleNotFound(CustomerNotFoundException exception, WebRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Customer not found", exception.getMessage(), request);
    }

    @ExceptionHandler(DuplicateCustomerException.class)
    ProblemDetail handleDuplicate(DuplicateCustomerException exception, WebRequest request) {
        return problem(HttpStatus.CONFLICT, "Customer already exists", exception.getMessage(), request);
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
