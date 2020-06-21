package com.jrmcdonald.customer.orchestration.api.model;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CustomerRequest {

    private final @NotNull String firstName;
    private final @NotNull String lastName;
}
