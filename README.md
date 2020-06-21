# Customer Orchestration Service

## Modules

### customer-orchestration-core
Core business logic of the service.

## customer-orchestration-endpoints:customer-orchestration-rest-api
Spring Boot application configuration and REST API endpoints.

## customer-orchestration-external:customer-orchestration-customer-entity
Logic for integrating with the customer-entity service.

## customer-orchestration-test:customer-orchestration-functional
SpringBootTest suites that perform end-to-end tests of the application against a mock entity api.

## Developer Notes

### Prerequisites

 * Docker
 * Java 11
 * Node 12 / Yarn (commit hooks - run `yarn` to install dependencies)
 * Helm
 * Skaffold (for developing against a Kubernetes cluster)

### IDE
To run in an IDE ensure that the `local` profile is set.

### Local Build
To build and run the tests:

> `./gradlew clean build`

To build the docker image:

> `./gradlew customer-orchestration-endpoints:customer-orchestration-rest-api:bootBuildImage`

To run the service locally:

> `SPRING_PROFILES_ACTIVE=local ./gradlew customer-orchestration-endpoints:customer-orchestration-rest-api:bootRun`

To run the service in a kubernetes cluster (with hot reloading):

> `skaffold dev --force=false`

### Sample Requests
Request an access token from Auth0 and export it to the `BEAERER_TOKEN` environment variable.

Register a customer:

> `curl -i -X POST http://localhost:8080/v1/customer/self -H "Authorization: Beaer ${BEARER_TOKEN}" -H "Content-Type: application/json" -d '{"firstName": "first", "lastName": "last"}'` 

Retrieve a customer:
 
> `curl -i http://localhost:8080/v1/customer/self -H "Authorization: Beaer ${BEARER_TOKEN}"` 

