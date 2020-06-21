package com.jrmcdonald.customer.orchestration.api.model;

import java.time.Instant;

import javax.validation.constraints.NotNull;

import lombok.Data;


@Data
public class CustomerResponse {

    private final @NotNull String id;
    private final @NotNull String firstName;
    private final @NotNull String lastName;
    private final @NotNull Instant createdAt;
}

