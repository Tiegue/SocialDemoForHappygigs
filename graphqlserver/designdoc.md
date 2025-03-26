# GraphQL WebFlux Application Design Document

## Overview
This document provides the design details of the **GraphQL WebFlux Application**, built using **Spring Boot**, **WebFlux**, and **GraphQL Java**. The application demonstrates the use of reactive programming with **GraphQL Queries, Mutations, and Subscriptions**.

## Architecture
The application follows a layered architecture:

1. **Controller Layer**: Handles GraphQL requests.
2. **Service Layer**: Manages business logic and data operations.
3. **Reactive Event System**: Uses **Sinks.Many<T>** for real-time message subscriptions.

## Dependencies
```xml
<dependencies>
    <!-- Spring Boot WebFlux -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <!-- Spring Boot GraphQL -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-graphql</artifactId>
    </dependency>
</dependencies>
```

## Components


## How to Run
1. **Start the application**
   ```sh
   mvn spring-boot:run
   ```
2. **Test GraphQL using Altair/Postman**
    - GraphQL Endpoint: `http://localhost:4446/graphql`
    - Send queries/mutations as defined above.

## Conclusion
This application demonstrates how to use **Spring Boot WebFlux** with **GraphQL**, leveraging **Flux and Mono** for real-time event streaming. 🚀

