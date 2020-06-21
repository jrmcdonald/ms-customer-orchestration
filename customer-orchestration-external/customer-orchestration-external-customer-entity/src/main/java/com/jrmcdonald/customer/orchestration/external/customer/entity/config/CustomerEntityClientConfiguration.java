package com.jrmcdonald.customer.orchestration.external.customer.entity.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(CustomerEntityConfigurationProperties.class)
public class CustomerEntityClientConfiguration {

    @Bean
    public WebClient webClient(WebClient.Builder builder,
                               ServerOAuth2AuthorizedClientExchangeFilterFunction authorizedClientFilter,
                               CustomerEntityConfigurationProperties properties) {
        return builder.filter(authorizedClientFilter)
                      .baseUrl(properties.getServiceUrl())
                      .build();
    }
}
