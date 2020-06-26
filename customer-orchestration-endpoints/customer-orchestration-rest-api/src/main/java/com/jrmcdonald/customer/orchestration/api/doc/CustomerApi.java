package com.jrmcdonald.customer.orchestration.api.doc;

import com.jrmcdonald.customer.orchestration.api.model.CustomerRequest;
import com.jrmcdonald.customer.orchestration.api.model.CustomerResponse;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestBody;

import reactor.core.publisher.Mono;

@Tag(name = "Customer API")
public interface CustomerApi {

    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401")
    @ApiResponse(responseCode = "403")
    @ApiResponse(responseCode = "404")
    @ApiResponse(responseCode = "500")
    Mono<ResponseEntity<CustomerResponse>> getCustomer();

    @ApiResponse(responseCode = "201", headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created customer account", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "401")
    @ApiResponse(responseCode = "403")
    @ApiResponse(responseCode = "409")
    @ApiResponse(responseCode = "500")
    Mono<ResponseEntity<CustomerResponse>> createCustomer(@RequestBody CustomerRequest customerRequest, ServerHttpRequest serverHttpRequest);
}
