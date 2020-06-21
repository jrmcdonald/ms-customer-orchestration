package com.jrmcdonald.customer.orchestration.external.customer.entity.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jrmcdonald.common.schema.definition.exception.ConflictException;
import com.jrmcdonald.common.schema.definition.exception.NotFoundException;
import com.jrmcdonald.common.schema.definition.exception.ServiceException;
import com.jrmcdonald.customer.orchestration.external.customer.entity.config.CustomerEntityConfigurationProperties;
import com.jrmcdonald.customer.orchestration.external.customer.entity.model.Customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Instant;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerEntityServiceClientTest {

    private static final String CUSTOMER_ID_VALUE = "customer-id-123";
    private static final String CUSTOMER_ENTITY_ENDPOINT = "/customer-entity/v1/customer/";

    @Mock
    CustomerEntityConfigurationProperties customerEntityConfigurationProperties;

    private CustomerEntityServiceClient customerEntityServiceClient;

    private MockWebServer mockWebServer;

    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        when(customerEntityConfigurationProperties.getServiceEndpoint()).thenReturn(CUSTOMER_ENTITY_ENDPOINT + "{id}");

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("").toString()).build();

        customerEntityServiceClient = new CustomerEntityServiceClient(customerEntityConfigurationProperties, webClient);
    }

    @AfterEach
    void afterEach() throws IOException {
        mockWebServer.close();
        verifyNoMoreInteractions(customerEntityConfigurationProperties);
    }

    @DisplayName("GET Customer Tests")
    @Nested
    class GetCustomerTests {

        @Test
        @DisplayName("Should get customer")
        void shouldGetCustomer() throws InterruptedException, JsonProcessingException {
            Customer expectedCustomer = new Customer();
            expectedCustomer.setId(CUSTOMER_ID_VALUE)
                            .setFirstName("first")
                            .setLastName("last")
                            .setCreatedAt(Instant.now());

            MockResponse customerResponse = new MockResponse();
            customerResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            customerResponse.setBody(objectMapper.writeValueAsString(expectedCustomer));

            mockWebServer.enqueue(customerResponse);

            StepVerifier.create(customerEntityServiceClient.getCustomer(CUSTOMER_ID_VALUE))
                        .assertNext(actualCustomer -> assertThat(actualCustomer).isEqualTo(expectedCustomer))
                        .verifyComplete();

            RecordedRequest actualRequest = mockWebServer.takeRequest();
            assertThat(actualRequest.getMethod()).isEqualTo(HttpMethod.GET.toString());
            assertThat(actualRequest.getPath()).isEqualTo(CUSTOMER_ENTITY_ENDPOINT + CUSTOMER_ID_VALUE);
        }

        @Test
        @DisplayName("Should return NotFoundException when service returns 404")
        void shouldReturnNotFoundExceptionWhenServiceReturns404() {
            MockResponse customerResponse = new MockResponse();
            customerResponse.setResponseCode(HttpStatus.NOT_FOUND.value());

            mockWebServer.enqueue(customerResponse);

            StepVerifier.create(customerEntityServiceClient.getCustomer(CUSTOMER_ID_VALUE))
                        .expectError(NotFoundException.class)
                        .verify();
        }

        @Test
        @DisplayName("Should return ServiceException when service returns 5xx")
        void shouldReturnServiceExceptionWhenServiceReturns5xx() {
            MockResponse customerResponse = new MockResponse();
            customerResponse.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            mockWebServer.enqueue(customerResponse);

            StepVerifier.create(customerEntityServiceClient.getCustomer(CUSTOMER_ID_VALUE))
                        .expectError(ServiceException.class)
                        .verify();
        }
    }

    @DisplayName("POST Customer Tests")
    @Nested
    class PostCustomerTests {

        @Test
        @DisplayName("Should create customer")
        void shouldCreateCustomer() throws InterruptedException, JsonProcessingException {
            Customer newCustomer = new Customer();
            newCustomer.setFirstName("first")
                       .setLastName("last");

            MockResponse customerResponse = new MockResponse();
            customerResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            customerResponse.setBody(objectMapper.writeValueAsString(newCustomer));

            mockWebServer.enqueue(customerResponse);

            StepVerifier.create(customerEntityServiceClient.createCustomer(CUSTOMER_ID_VALUE, newCustomer))
                        .assertNext(actualCustomer -> assertThat(actualCustomer).isEqualTo(newCustomer))
                        .verifyComplete();

            RecordedRequest actualRequest = mockWebServer.takeRequest();
            assertThat(actualRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
            assertThat(actualRequest.getPath()).isEqualTo(CUSTOMER_ENTITY_ENDPOINT + CUSTOMER_ID_VALUE);
            assertThat(objectMapper.readValue(actualRequest.getBody().readUtf8(), Customer.class)).isEqualTo(newCustomer);
        }

        @Test
        @DisplayName("Should return ConflictException when service returns 409")
        void shouldReturnConflictExceptionWhenServiceReturns409() {
            MockResponse customerResponse = new MockResponse();
            customerResponse.setResponseCode(HttpStatus.CONFLICT.value());

            mockWebServer.enqueue(customerResponse);

            StepVerifier.create(customerEntityServiceClient.createCustomer(CUSTOMER_ID_VALUE, new Customer()))
                        .expectError(ConflictException.class)
                        .verify();
        }

        @Test
        @DisplayName("Should return ServiceException when service returns 5xx")
        void shouldReturnServiceExceptionWhenServiceReturns5xx() {
            MockResponse customerResponse = new MockResponse();
            customerResponse.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            mockWebServer.enqueue(customerResponse);

            StepVerifier.create(customerEntityServiceClient.createCustomer(CUSTOMER_ID_VALUE, new Customer()))
                        .expectError(ServiceException.class)
                        .verify();
        }
    }
}