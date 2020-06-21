package com.jrmcdonald.customer.orchestration.api.config;

import com.jrmcdonald.common.ext.spring.core.oauth2.config.JwtValidatorConfiguration;
import com.jrmcdonald.common.ext.spring.datetime.config.DateTimeConfiguration;
import com.jrmcdonald.common.ext.spring.reactive.context.config.ReactiveContextLifterConfiguration;
import com.jrmcdonald.common.ext.spring.reactive.filter.config.ReactiveFilterConfiguration;
import com.jrmcdonald.common.ext.spring.reactive.oauth2.client.credentials.config.ReactiveClientCredentialsConfiguration;
import com.jrmcdonald.common.ext.spring.reactive.oauth2.jwt.config.ReactiveJwtDecoderConfiguration;
import com.jrmcdonald.common.ext.spring.reactive.security.authentication.config.ReactiveAuthenticationConfiguration;
import com.jrmcdonald.customer.orchestration.external.customer.entity.config.CustomerEntityClientConfiguration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan(basePackages = "com.jrmcdonald.customer.orchestration")
@Import({
        CustomerEntityClientConfiguration.class,
        DateTimeConfiguration.class,
        JwtValidatorConfiguration.class,
        ReactiveContextLifterConfiguration.class,
        ReactiveClientCredentialsConfiguration.class,
        ReactiveFilterConfiguration.class,
        ReactiveJwtDecoderConfiguration.class,
        ReactiveAuthenticationConfiguration.class
})
public class ApplicationConfiguration {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
