package com.service_management_platform.appointment.infrastructure.customer;

import com.service_management_platform.appointment.application.CustomerNotFoundException;
import com.service_management_platform.appointment.application.CustomerServiceUnavailableException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class CustomerClient {
    private final RestClient restClient;

    public CustomerClient(
            RestClient.Builder builder,
            @Value("${clients.customer-service.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public void ensureCustomerExists(UUID customerId) {
        try {
            restClient.get()
                    .uri("/api/customers/{id}", customerId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            throw new CustomerNotFoundException(customerId);
                        }
                        throw new CustomerServiceUnavailableException(null);
                    })
                    .toBodilessEntity();
        } catch (CustomerNotFoundException | CustomerServiceUnavailableException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new CustomerServiceUnavailableException(exception);
        }
    }
}
