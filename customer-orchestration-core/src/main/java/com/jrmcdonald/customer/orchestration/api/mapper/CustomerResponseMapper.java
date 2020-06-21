package com.jrmcdonald.customer.orchestration.api.mapper;

import com.jrmcdonald.customer.orchestration.api.model.CustomerResponse;
import com.jrmcdonald.customer.orchestration.external.customer.entity.model.Customer;

import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerResponseMapper implements Function<Customer, CustomerResponse> {

    @Override
    public CustomerResponse apply(Customer customer) {
        return new CustomerResponse(customer.getId(),
                                    customer.getFirstName(),
                                    customer.getLastName(),
                                    customer.getCreatedAt());
    }
}
