package com.jrmcdonald.customer.orchestration.api.mapper;

import com.jrmcdonald.customer.orchestration.api.model.CustomerRequest;
import com.jrmcdonald.customer.orchestration.external.customer.entity.model.Customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRequestMapperTest {

    private CustomerRequestMapper customerRequestMapper;

    @BeforeEach
    void beforeEach() {
        customerRequestMapper = new CustomerRequestMapper();
    }

    @Test
    @DisplayName("Should map customer id and CustomerRequest to Customer")
    void shouldMapCustomerIdAndCustomerRequestToCustomer() {
        CustomerRequest customerRequest = new CustomerRequest("first", "last");

        Customer actualCustomer = customerRequestMapper.apply(customerRequest);

        assertThat(actualCustomer.getId()).isNull();
        assertThat(actualCustomer.getFirstName()).isEqualTo("first");
        assertThat(actualCustomer.getLastName()).isEqualTo("last");
        assertThat(actualCustomer.getCreatedAt()).isNull();
    }

}