package com.jrmcdonald.customer.orchestration.api.service;

import com.jrmcdonald.common.ext.spring.reactive.security.authentication.ReactiveAuthenticationFacade;
import com.jrmcdonald.customer.orchestration.api.mapper.CustomerRequestMapper;
import com.jrmcdonald.customer.orchestration.api.mapper.CustomerResponseMapper;
import com.jrmcdonald.customer.orchestration.api.model.CustomerRequest;
import com.jrmcdonald.customer.orchestration.api.model.CustomerResponse;
import com.jrmcdonald.customer.orchestration.external.customer.entity.client.CustomerEntityServiceClient;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final ReactiveAuthenticationFacade authenticationFacade;
    private final CustomerEntityServiceClient customerEntityServiceClient;
    private final CustomerRequestMapper customerRequestMapper;
    private final CustomerResponseMapper customerResponseMapper;

    public Mono<CustomerResponse> getCustomer() {
        return authenticationFacade.getCustomerId()
                                   .flatMap(customerEntityServiceClient::getCustomer)
                                   .map(customerResponseMapper);
    }

    public Mono<CustomerResponse> createCustomer(CustomerRequest customerRequest) {
        return authenticationFacade.getCustomerId()
                                   .flatMap(customerId -> customerEntityServiceClient.createCustomer(customerId, customerRequestMapper.apply(customerRequest)))
                                   .map(customerResponseMapper);
    }
}
