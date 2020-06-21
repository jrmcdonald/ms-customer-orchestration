package com.jrmcdonald.customer.orchestration.api.controller;

import com.jrmcdonald.common.schema.definition.exception.ConflictException;
import com.jrmcdonald.common.schema.definition.exception.NotFoundException;
import com.jrmcdonald.customer.orchestration.api.model.CustomerRequest;
import com.jrmcdonald.customer.orchestration.api.model.CustomerResponse;
import com.jrmcdonald.customer.orchestration.api.service.CustomerService;

import io.micrometer.core.annotation.Timed;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Timed
    @GetMapping(path = "/self", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CustomerResponse>> getCustomer() {
        return customerService.getCustomer()
                              .map(ResponseEntity::ok)
                              .onErrorResume(e -> {
                                  if (e instanceof NotFoundException) {
                                      return Mono.just(ResponseEntity.notFound().build());
                                  } else {
                                      log.error("An unexpected error occurred", e);
                                      return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                                  }
                              });
    }

    @Timed
    @PostMapping(path = "/self", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CustomerResponse>> createCustomer(@RequestBody CustomerRequest customerRequest) {
        return customerService.createCustomer(customerRequest)
                              .map(ResponseEntity::ok)
                              .onErrorResume(e -> {
                                  if (e instanceof ConflictException) {
                                      return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                                  } else {
                                      log.error("An unexpected error occurred", e);
                                      return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                                  }
                              });
    }
}
