package com.jrmcdonald.customer.orchestration.external.customer.entity.client;

import com.jrmcdonald.common.ext.spring.reactive.error.ErrorPredicates;
import com.jrmcdonald.customer.orchestration.external.customer.entity.config.CustomerEntityConfigurationProperties;
import com.jrmcdonald.customer.orchestration.external.customer.entity.model.Customer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CustomerEntityServiceClient {

    private final CustomerEntityConfigurationProperties customerEntityConfigurationProperties;
    private final WebClient webClient;

    public CustomerEntityServiceClient(CustomerEntityConfigurationProperties customerEntityConfigurationProperties, WebClient webClient) {
        this.customerEntityConfigurationProperties = customerEntityConfigurationProperties;
        this.webClient = webClient;
    }

    public Mono<Customer> getCustomer(String customerId) {
        return webClient.get()
                        .uri(customerEntityUri(customerId))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .onStatus(HttpStatus.NOT_FOUND::equals, ErrorPredicates::notFoundException)
                        .onStatus(HttpStatus::is5xxServerError, ErrorPredicates::serviceException)
                        .bodyToMono(Customer.class)
                        .doOnSuccess(customerSignal -> log.info("Retrieved customer"))
                        .doOnError(error -> log.error("Error retrieving customer: {}", error.getMessage()));
    }

    public Mono<Customer> createCustomer(String customerId, Customer newCustomer) {
        return webClient.post()
                        .uri(customerEntityUri(customerId))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(newCustomer)
                        .retrieve()
                        .onStatus(HttpStatus.CONFLICT::equals, ErrorPredicates::conflictException)
                        .onStatus(HttpStatus::is5xxServerError, ErrorPredicates::serviceException)
                        .bodyToMono(Customer.class)
                        .doOnSuccess(customerSignal -> log.info("Created customer"))
                        .doOnError(error -> log.error("Error creating customer: {}", error.getMessage()));
    }

    private Function<UriBuilder, URI> customerEntityUri(String customerId) {
        return builder -> builder.path(customerEntityConfigurationProperties.getServiceEndpoint()).build(customerId);
    }
}