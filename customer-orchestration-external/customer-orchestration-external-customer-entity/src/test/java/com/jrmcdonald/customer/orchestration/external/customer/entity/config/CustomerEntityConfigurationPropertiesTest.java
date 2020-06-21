package com.jrmcdonald.customer.orchestration.external.customer.entity.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerEntityConfigurationPropertiesTest {

    @Test
    @DisplayName("Should bind service-url when supplied")
    void shouldBindServiceUrlWhenSupplied() {

        Map<String, String> properties = new HashMap<>();
        properties.put("service-url", "url");

        Binder binder = new Binder(new MapConfigurationPropertySource(properties));
        BindResult<CustomerEntityConfigurationProperties> bindResult = binder.bind("", Bindable.of(CustomerEntityConfigurationProperties.class));

        assertThat(bindResult.get().getServiceUrl()).isEqualTo("url");
    }

    @Test
    @DisplayName("Should bind service-endpoint when supplied")
    void shouldBindServiceEndpointWhenSupplied() {

        Map<String, String> properties = new HashMap<>();
        properties.put("service-endpoint", "endpoint");

        Binder binder = new Binder(new MapConfigurationPropertySource(properties));
        BindResult<CustomerEntityConfigurationProperties> bindResult = binder.bind("", Bindable.of(CustomerEntityConfigurationProperties.class));

        assertThat(bindResult.get().getServiceEndpoint()).isEqualTo("endpoint");
    }
}