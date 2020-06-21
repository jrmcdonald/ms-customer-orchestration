package com.jrmcdonald.customer.orchestration.api.service;

import com.jrmcdonald.common.ext.spring.reactive.security.authentication.ReactiveAuthenticationFacade;
import com.jrmcdonald.common.schema.definition.exception.ConflictException;
import com.jrmcdonald.common.schema.definition.exception.NotFoundException;
import com.jrmcdonald.customer.orchestration.api.mapper.CustomerRequestMapper;
import com.jrmcdonald.customer.orchestration.api.mapper.CustomerResponseMapper;
import com.jrmcdonald.customer.orchestration.api.model.CustomerRequest;
import com.jrmcdonald.customer.orchestration.api.model.CustomerResponse;
import com.jrmcdonald.customer.orchestration.external.customer.entity.client.CustomerEntityServiceClient;
import com.jrmcdonald.customer.orchestration.external.customer.entity.model.Customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private static final String CUSTOMER_ID_VALUE = "customer-id-123";
    @Mock
    private ReactiveAuthenticationFacade authenticationFacade;

    @Mock
    private CustomerResponseMapper customerResponseMapper;

    @Mock
    private CustomerRequestMapper customerRequestMapper;

    @Mock
    private CustomerEntityServiceClient customerEntityServiceClient;

    private CustomerService customerService;

    @BeforeEach
    void beforeEach() {
        customerService = new CustomerService(authenticationFacade, customerEntityServiceClient, customerRequestMapper, customerResponseMapper);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(customerResponseMapper, customerRequestMapper, customerEntityServiceClient);
    }

    @Test
    @DisplayName("Should find the customer")
    void shouldFindTheCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID_VALUE)
                .setFirstName("first")
                .setLastName("last")
                .setCreatedAt(Instant.now());

        CustomerResponse expectedCustomerResponse = new CustomerResponse(customer.getId(),
                                                                         customer.getFirstName(),
                                                                         customer.getLastName(),
                                                                         customer.getCreatedAt());

        when(authenticationFacade.getCustomerId()).thenReturn(Mono.just(CUSTOMER_ID_VALUE));
        when(customerEntityServiceClient.getCustomer(eq(CUSTOMER_ID_VALUE))).thenReturn(Mono.just(customer));
        when(customerResponseMapper.apply(eq(customer))).thenReturn(expectedCustomerResponse);

        StepVerifier.create(customerService.getCustomer())
                    .assertNext(customerResponse -> assertThat(customerResponse).isEqualTo(expectedCustomerResponse))
                    .verifyComplete();
    }

    @Test
    @DisplayName("Should return NotFoundException")
    void shouldReturnNotFoundException() {
        when(authenticationFacade.getCustomerId()).thenReturn(Mono.just(CUSTOMER_ID_VALUE));
        when(customerEntityServiceClient.getCustomer(eq(CUSTOMER_ID_VALUE))).thenReturn(Mono.error(new NotFoundException()));

        StepVerifier.create(customerService.getCustomer())
                    .expectError(NotFoundException.class)
                    .verify();
    }

    @Test
    @DisplayName("Should create a customer profile")
    void shouldCreateACustomerProfile() {
        CustomerRequest customerRequest = new CustomerRequest("first", "last");

        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID_VALUE)
                .setFirstName(customerRequest.getFirstName())
                .setLastName(customerRequest.getLastName());

        Customer savedCustomer = new Customer();
        savedCustomer.setId(CUSTOMER_ID_VALUE)
                     .setFirstName(customerRequest.getFirstName())
                     .setLastName(customerRequest.getLastName())
                     .setCreatedAt(Instant.now());

        CustomerResponse expectedCustomerResponse = new CustomerResponse(savedCustomer.getId(),
                                                                         savedCustomer.getFirstName(),
                                                                         savedCustomer.getLastName(),
                                                                         savedCustomer.getCreatedAt());

        when(authenticationFacade.getCustomerId()).thenReturn(Mono.just(CUSTOMER_ID_VALUE));
        when(customerRequestMapper.apply(eq(customerRequest))).thenReturn(customer);
        when(customerEntityServiceClient.createCustomer(eq(CUSTOMER_ID_VALUE), eq(customer))).thenReturn(Mono.just(savedCustomer));
        when(customerResponseMapper.apply(eq(savedCustomer))).thenReturn(expectedCustomerResponse);

        StepVerifier.create(customerService.createCustomer(customerRequest))
                    .assertNext(customerResponse -> assertThat(customerResponse).isEqualTo(expectedCustomerResponse))
                    .verifyComplete();
    }

    @Test
    @DisplayName("Should return ConflictException")
    void shouldReturnConflictException() {
        when(authenticationFacade.getCustomerId()).thenReturn(Mono.just(CUSTOMER_ID_VALUE));
        when(customerRequestMapper.apply(any(CustomerRequest.class))).thenReturn(new Customer());
        when(customerEntityServiceClient.createCustomer(eq(CUSTOMER_ID_VALUE), any(Customer.class))).thenReturn(Mono.error(new ConflictException()));

        StepVerifier.create(customerService.createCustomer(new CustomerRequest("first", "last")))
                    .expectError(ConflictException.class)
                    .verify();
    }
}