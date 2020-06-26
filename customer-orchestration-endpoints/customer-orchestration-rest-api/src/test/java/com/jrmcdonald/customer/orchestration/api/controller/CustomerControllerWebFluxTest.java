package com.jrmcdonald.customer.orchestration.api.controller;

import com.jrmcdonald.customer.orchestration.api.model.CustomerRequest;
import com.jrmcdonald.customer.orchestration.api.model.CustomerResponse;
import com.jrmcdonald.customer.orchestration.api.service.CustomerService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(controllers = CustomerController.class)
@ActiveProfiles("unit-test")
class CustomerControllerWebFluxTest {

    private static final String CUSTOMER_ORCHESTRATION_ENDPOINT = "/v1/customer/self";
    private static final String CUSTOMER_ID_VALUE = "customer-id-123";

    @MockBean
    private CustomerService customerService;

    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(customerService);
    }

    @Nested
    class GetCustomerTests {

        @Test
        @DisplayName("Should return authenticated customer profile")
        void shouldReturnAuthenticatedCustomerProfile() {
            CustomerResponse expectedResponse = new CustomerResponse(CUSTOMER_ID_VALUE,
                                                                     "first",
                                                                     "last",
                                                                     Instant.now());

            when(customerService.getCustomer()).thenReturn(Mono.just(expectedResponse));

            Flux<CustomerResponse> result = webTestClient.mutateWith(mockJwt())
                                                         .get().uri(CUSTOMER_ORCHESTRATION_ENDPOINT)
                                                         .accept(MediaType.APPLICATION_JSON)
                                                         .exchange()
                                                         .expectStatus().isOk()
                                                         .returnResult(CustomerResponse.class)
                                                         .getResponseBody();

            StepVerifier.create(result)
                        .assertNext(actualResponse -> assertThat(actualResponse).isEqualTo(expectedResponse))
                        .verifyComplete();

            verify(customerService).getCustomer();
        }
    }

    @Nested
    class CreateCustomerTests {

        @Test
        @DisplayName("Should return created customer profile")
        void shouldReturnCreatedCustomerProfile() {
            CustomerResponse expectedResponse = new CustomerResponse(CUSTOMER_ID_VALUE,
                                                                     "first",
                                                                     "last",
                                                                     Instant.now());

            CustomerRequest customerRequest = new CustomerRequest("first", "last");

            when(customerService.createCustomer(refEq(customerRequest))).thenReturn(Mono.just(expectedResponse));

            Flux<CustomerResponse> result = webTestClient.mutateWith(mockJwt())
                                                         .mutateWith(csrf())
                                                         .post().uri(CUSTOMER_ORCHESTRATION_ENDPOINT)
                                                         .bodyValue(customerRequest)
                                                         .exchange()
                                                         .expectStatus().isCreated()
                                                         .expectHeader().valueEquals(HttpHeaders.LOCATION, CUSTOMER_ORCHESTRATION_ENDPOINT)
                                                         .returnResult(CustomerResponse.class)
                                                         .getResponseBody();

            StepVerifier.create(result)
                        .assertNext(actualResponse -> assertThat(actualResponse).isEqualTo(expectedResponse))
                        .verifyComplete();

            verify(customerService).createCustomer(refEq(customerRequest));
        }
    }
}