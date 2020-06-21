package com.jrmcdonald.customer.orchestration.api.mapper;

import com.jrmcdonald.customer.orchestration.api.model.CustomerRequest;
import com.jrmcdonald.customer.orchestration.external.customer.entity.model.Customer;

import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerRequestMapper implements Function<CustomerRequest, Customer> {

    @Override
    public Customer apply(CustomerRequest customerRequest) {
        Customer mappedCustomer = new Customer();
        return mappedCustomer.setFirstName(customerRequest.getFirstName())
                             .setLastName(customerRequest.getLastName());
    }
}
