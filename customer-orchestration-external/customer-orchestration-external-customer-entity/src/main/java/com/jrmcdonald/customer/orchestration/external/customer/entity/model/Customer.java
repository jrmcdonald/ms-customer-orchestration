package com.jrmcdonald.customer.orchestration.external.customer.entity.model;

import java.time.Instant;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private @NotNull String id;
    private @NotNull String firstName;
    private @NotNull String lastName;
    private @NotNull Instant createdAt;
}
