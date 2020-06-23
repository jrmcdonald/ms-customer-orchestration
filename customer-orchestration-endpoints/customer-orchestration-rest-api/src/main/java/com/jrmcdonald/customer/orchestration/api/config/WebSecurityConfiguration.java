package com.jrmcdonald.customer.orchestration.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
            .pathMatchers("/v1/customer/**").authenticated()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/**").denyAll()
            .and()
            .csrf().disable()
            .oauth2ResourceServer().jwt();

        return http.build();
    }
}
