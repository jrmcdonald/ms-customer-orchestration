package com.jrmcdonald.orchestration.api.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jrmcdonald.customer.orchestration.api.Application;
import com.jrmcdonald.customer.orchestration.api.model.CustomerResponse;
import com.jrmcdonald.customer.orchestration.external.customer.entity.model.Customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.time.Instant;

import lombok.Data;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("functional-test")
public class CustomerOrchestrationSpringBootTest {

    private static final String CUSTOMER_ID_VALUE = "user";
    private static final String CUSTOMER_ENTITY_ENDPOINT = "/customer-entity/v1/customer/";
    private static final String CUSTOMER_ORCHESTRATION_ENDPOINT = "/v1/customer/self";
    private static final int MOCK_ENTITY_PORT = 8090;
    private static final int MOCK_AUTH0_PORT = 8091;

    @Autowired
    private WebTestClient webTestClient;

    private MockWebServer mockCustomerEntityServer;

    private MockWebServer mockAuth0Server;

    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockCustomerEntityServer = new MockWebServer();
        mockCustomerEntityServer.start(MOCK_ENTITY_PORT);

        // TODO: This should be able to be replaced by .mutateWith(mockOAuth2Client()) on the WebTestClient
        mockAuth0Server = new MockWebServer();
        mockAuth0Server.start(MOCK_AUTH0_PORT);

        MockResponse tokenResponse = new MockResponse();
        tokenResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        tokenResponse.setBody(objectMapper.writeValueAsString(new TokenResponse()));

        mockAuth0Server.enqueue(tokenResponse);
    }

    @AfterEach
    void afterEach() throws IOException {
        mockCustomerEntityServer.close();
        mockAuth0Server.close();
    }

    @Data
    private static class TokenResponse {

        private final String token_type = "Bearer";
        private final String access_token = "some-token-value";
        private final long expires_in = 3600;
    }

    @DisplayName("GET /v1/customer/self Tests")
    @Nested
    class GetV1CustomerSelfTests {

        @Test
        @DisplayName("Should reject anonymous user")
        void shouldRejectAnonymousUser() {
            webTestClient.get().uri(CUSTOMER_ORCHESTRATION_ENDPOINT)
                         .accept(MediaType.APPLICATION_JSON)
                         .exchange()
                         .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("Should get existing customer")
        void shouldGetExistingCustomer() throws Exception {
            Customer expectedCustomer = new Customer();
            expectedCustomer.setId("user")
                            .setFirstName("first")
                            .setLastName("last")
                            .setCreatedAt(Instant.now());

            MockResponse customerEntityResponse = new MockResponse();
            customerEntityResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            customerEntityResponse.setBody(objectMapper.writeValueAsString(expectedCustomer));

            mockCustomerEntityServer.enqueue(customerEntityResponse);

            CustomerResponse expectedResponse = new CustomerResponse(expectedCustomer.getId(),
                                                                     expectedCustomer.getFirstName(),
                                                                     expectedCustomer.getLastName(),
                                                                     expectedCustomer.getCreatedAt());

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

            RecordedRequest actualCustomerEntityRequest = mockCustomerEntityServer.takeRequest();
            assertThat(actualCustomerEntityRequest.getMethod()).isEqualTo(HttpMethod.GET.toString());
            assertThat(actualCustomerEntityRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer some-token-value");
            assertThat(actualCustomerEntityRequest.getPath()).isEqualTo(CUSTOMER_ENTITY_ENDPOINT + CUSTOMER_ID_VALUE);
        }

        @Test
        @DisplayName("Should return 404 Not Found for non-existing customer")
        void shouldReturn404NotFoundForNonExistingCustomer() throws Exception {
            MockResponse customerEntityResponse = new MockResponse();
            customerEntityResponse.setResponseCode(HttpStatus.NOT_FOUND.value());

            mockCustomerEntityServer.enqueue(customerEntityResponse);

            webTestClient.mutateWith(mockJwt())
                         .get().uri(CUSTOMER_ORCHESTRATION_ENDPOINT)
                         .accept(MediaType.APPLICATION_JSON)
                         .exchange()
                         .expectStatus().isNotFound();

            RecordedRequest actualCustomerEntityRequest = mockCustomerEntityServer.takeRequest();
            assertThat(actualCustomerEntityRequest.getMethod()).isEqualTo(HttpMethod.GET.toString());
            assertThat(actualCustomerEntityRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer some-token-value");
            assertThat(actualCustomerEntityRequest.getPath()).isEqualTo(CUSTOMER_ENTITY_ENDPOINT + CUSTOMER_ID_VALUE);
        }
    }

    @DisplayName("POST /v1/customer/self Tests")
    @Nested
    class PostV1CustomerSelfTests {

        @Test
        @DisplayName("Should reject anonymous user")
        void shouldRejectAnonymousUser() {
            webTestClient.get()
                         .uri(CUSTOMER_ORCHESTRATION_ENDPOINT)
                         .exchange()
                         .expectStatus()
                         .isUnauthorized();
        }

        @Test
        @DisplayName("Should register new customer")
        void shouldRegisterNewCustomer() throws Exception {
            Customer newCustomer = new Customer();
            newCustomer.setFirstName("first")
                       .setLastName("last");

            Customer expectedCustomer = new Customer();
            expectedCustomer.setId(CUSTOMER_ID_VALUE)
                            .setFirstName(newCustomer.getFirstName())
                            .setLastName(newCustomer.getLastName())
                            .setCreatedAt(Instant.now());

            CustomerResponse expectedResponse = new CustomerResponse(expectedCustomer.getId(),
                                                                     expectedCustomer.getFirstName(),
                                                                     expectedCustomer.getLastName(),
                                                                     expectedCustomer.getCreatedAt());

            MockResponse customerEntityResponse = new MockResponse();
            customerEntityResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            customerEntityResponse.setBody(objectMapper.writeValueAsString(expectedCustomer));

            mockCustomerEntityServer.enqueue(customerEntityResponse);

            Flux<CustomerResponse> result = webTestClient.mutateWith(mockJwt())
                                                         .post().uri(CUSTOMER_ORCHESTRATION_ENDPOINT)
                                                         .accept(MediaType.APPLICATION_JSON)
                                                         .bodyValue(newCustomer)
                                                         .exchange()
                                                         .expectStatus().isOk()
                                                         .returnResult(CustomerResponse.class)
                                                         .getResponseBody();

            StepVerifier.create(result)
                        .assertNext(actualResponse -> assertThat(actualResponse).isEqualTo(expectedResponse))
                        .verifyComplete();

            RecordedRequest actualCustomerEntityRequest = mockCustomerEntityServer.takeRequest();
            assertThat(actualCustomerEntityRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
            assertThat(actualCustomerEntityRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer some-token-value");
            assertThat(actualCustomerEntityRequest.getPath()).isEqualTo(CUSTOMER_ENTITY_ENDPOINT + CUSTOMER_ID_VALUE);
            assertThat(objectMapper.readValue(actualCustomerEntityRequest.getBody().readUtf8(), Customer.class)).isEqualTo(newCustomer);
        }

        @Test
        @DisplayName("Should return 409 Conflict for existing customer")
        void shouldReturn409ConflictForExistingCustomer() throws Exception {
            Customer newCustomer = new Customer();
            newCustomer.setFirstName("first")
                       .setLastName("last");

            MockResponse customerEntityResponse = new MockResponse();
            customerEntityResponse.setResponseCode(HttpStatus.CONFLICT.value());

            mockCustomerEntityServer.enqueue(customerEntityResponse);

            webTestClient.mutateWith(mockJwt())
                         .post().uri(CUSTOMER_ORCHESTRATION_ENDPOINT)
                         .accept(MediaType.APPLICATION_JSON)
                         .bodyValue(newCustomer)
                         .accept(MediaType.APPLICATION_JSON)
                         .exchange()
                         .expectStatus().isEqualTo(HttpStatus.CONFLICT);

            RecordedRequest actualCustomerEntityRequest = mockCustomerEntityServer.takeRequest();
            assertThat(actualCustomerEntityRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
            assertThat(actualCustomerEntityRequest.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer some-token-value");
            assertThat(actualCustomerEntityRequest.getPath()).isEqualTo(CUSTOMER_ENTITY_ENDPOINT + CUSTOMER_ID_VALUE);
            assertThat(objectMapper.readValue(actualCustomerEntityRequest.getBody().readUtf8(), Customer.class)).isEqualTo(newCustomer);
        }
    }
}

