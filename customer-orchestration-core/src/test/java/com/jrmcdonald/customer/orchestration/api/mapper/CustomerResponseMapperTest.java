package com.jrmcdonald.customer.orchestration.api.mapper;

import com.jrmcdonald.customer.orchestration.api.model.CustomerResponse;
import com.jrmcdonald.customer.orchestration.external.customer.entity.model.Customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerResponseMapperTest {

    CustomerResponseMapper customerResponseMapper;

    @BeforeEach
    void beforeEach() {
        customerResponseMapper = new CustomerResponseMapper();
    }

    @Test
    @DisplayName("Should map Customer to CustomerResponse")
    void shouldMapCustomerToCustomerResponse() {
        Customer customer = new Customer();
        customer.setId("customer-id-123")
                .setFirstName("first")
                .setLastName("last")
                .setCreatedAt(Instant.now());

        CustomerResponse customerResponse = customerResponseMapper.apply(customer);

        assertThat(customerResponse.getId()).isEqualTo(customer.getId());
        assertThat(customerResponse.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(customerResponse.getLastName()).isEqualTo(customer.getLastName());
        assertThat(customerResponse.getCreatedAt()).isEqualTo(customer.getCreatedAt());
    }
}