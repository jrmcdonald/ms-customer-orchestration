package com.jrmcdonald.customer.orchestration.external.customer.entity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "customer.entity")
public class CustomerEntityConfigurationProperties {

    private final String serviceUrl;
    private final String serviceEndpoint;
}
