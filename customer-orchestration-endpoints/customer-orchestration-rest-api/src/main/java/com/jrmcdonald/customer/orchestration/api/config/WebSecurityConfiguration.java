package com.jrmcdonald.customer.orchestration.api.config;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
            .matchers(EndpointRequest.toAnyEndpoint()).permitAll()
            .pathMatchers("/v1/customer/**").authenticated()
            .pathMatchers("/**").denyAll()
            .and()
            .csrf().disable()
//            .oauth2Client(withDefaults())
            .oauth2ResourceServer().jwt();

        return http.build();
    }
}
