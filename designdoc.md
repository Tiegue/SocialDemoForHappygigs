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

### 1️⃣ **Message Model**
```java
class Message {
    private String content;
    
    public Message(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
}
```

### 2️⃣ **Message Service**
```java
@Service
class MessageService {
    private final List<Message> messages = new ArrayList<>();
    private final Sinks.Many<Message> sink;

    public MessageService(Sinks.Many<Message> sink) {
        this.sink = sink;
    }

    public Flux<Message> getMessages() {
        return Flux.fromIterable(messages);
    }

    public Mono<Message> addMessage(String content) {
        Message message = new Message(content);
        messages.add(message);
        sink.tryEmitNext(message);
        return Mono.just(message);
    }

    public Flux<Message> subscribeMessages() {
        return sink.asFlux();
    }
}
```

### 3️⃣ **Message Controller (GraphQL Endpoint)**
```java
@Controller
class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @QueryMapping
    public Flux<Message> messages() {
        return messageService.getMessages();
    }

    @MutationMapping
    public Mono<Message> addMessage(@Argument String content) {
        return messageService.addMessage(content);
    }

    @SubscriptionMapping
    public Flux<Message> messageSubscription() {
        return messageService.subscribeMessages();
    }
}
```

### 4️⃣ **GraphQL Schema (schema.graphqls)**
```graphql
type Message {
    content: String
}

type Query {
    messages: [Message]
}

type Mutation {
    addMessage(content: String!): Message
}

type Subscription {
    messageSubscription: Message
}
```

## API Endpoints
| Type        | Operation          | GraphQL Query |
|------------|-------------------|--------------|
| **Query** | Get messages      | `{ messages { content } }` |
| **Mutation** | Add a message  | `mutation { addMessage(content: "Hello") { content } }` |
| **Subscription** | Listen to messages | `subscription { messageSubscription { content } }` |

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

