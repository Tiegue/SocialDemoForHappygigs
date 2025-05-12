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

# **HappyGigs Social Module Design Document (Revised)**

## **1. Overview**
The **Social Module** in HappyGigs is an independent microservice that enables **real-time venue-based social interactions** using **GraphQL mutations/subscriptions**, **Kafka**, **Redis**, and **PostgreSQL**.

### **Key Features:**
- 🧭 **User presence and activity tracking:**
   - Detect when a user enters or leaves a venue.
   - Track user visit history in PostgreSQL via `UserVenueVisitingLog`.
   - Implemented: `UserEnteredEvent` and `UserLeftEvent` via Kafka.

- 🔔 **Real-time notifications and interactions:**
   - Notify existing users when someone enters.
   - Deliver user lists and chat updates via GraphQL Subscriptions.
   - Buzz feature: send device-triggered alerts when a new user joins.

- 💬 **Private chat system:**
   - One-on-one encrypted messaging between users in the same venue.
   - Chat history stored in PostgreSQL (`ChatHistory`).
   - Direct message routing using per-user Sink mapping.

- 🧠 **Reactive, scalable backend pipeline:**
   - GraphQL Mutation → VenueTrackerService → KafkaProducer + Redis
   - KafkaEventConsumer → SocialSubscription → WebSocket Subscriptions

- 📦 **Optimized storage and transport:**
   - Redis for real-time presence and optional geospatial matching.
   - PostgreSQL for persistent logs and chat history.
   - Kafka for event-based decoupling and scaling.

---

## **2. System Architecture**

### **2.1 Interaction Flow (Clean)**
```plaintext
GraphQL Mutation (sendChatMessage / userEnteredVenue)
        ↓
VenueTrackerService (validates input, logic)
        ↓
KafkaProducer + Redis (publishes events, updates presence)
        ↓
KafkaEventConsumer (reacts to events)
        ↓
SocialSubscription.pushToSink() (real-time delivery to frontend via GraphQL Subscriptions)
```

---

## **3. Data Models**

### **3.0 UserVenueVisitingLog (Visit History Record)**
```java
public class UserVenueVisitingLog {
    private UUID id;
    private String userId;
    private String venueId;
    private String action; // "ENTERED" or "LEFT"
    private long timestamp;
}
```

### **3.1 Message (System Message)**
```java
public class Message {
    private String sender;
    private String receiver;
    private String content;
    private String timestamp;
}
```

### **3.2 ChatMessageEvent (Private Chat Message)**
```java
public class ChatMessageEvent {
    private String senderId;
    private String receiverId;
    private String venueId;
    private String content;
    private long timestamp;
}
```

### **3.3 VenuePresence**
```java
public class VenuePresence {
    private String venueId;
    private Set<String> users;
}
```

### **3.4 ChatHistory**
```java
public class ChatHistory {
    private UUID id;
    private String sender;
    private String receiver;
    private String venueId;
    private String encryptedMessage;
    private long timestamp;
}
```

---

## **4. GraphQL API**

### **Mutations**
```graphql
type Mutation {
  userEnteredVenue(userId: String!, venueId: String!): String
  userLeftVenue(userId: String!, venueId: String!): String
  sendChatMessage(senderId: String!, receiverId: String!, venueId: String!, content: String!): String
}
```

### **Subscriptions**
```graphql
type Subscription {
  receiveUserList(userId: String!): [String]
  receiveMessages(userId: String!): Message
  receiveChatMessages(userId: String!): ChatMessageEvent
  receiveBuzz(userId: String!): Boolean
}
```

---

## **5. Kafka Topics**
- `user-entered`
- `user-left`
- `chat-messages`

---

## **6. Redis Usage**
- Redis Set to track venue presence.
- Redis Geo to match user location with venue (optional).

---

## **7. PostgreSQL Usage**
- Stores encrypted chat messages per venue.
- Persists user activity logs as `UserVenueVisitingLog` — essential for full visit traceability.
- This includes all `user-entered` and `user-left` events, which are captured via Kafka and stored reliably for auditing and analytics.

---

## **8. Security and Encryption**
- AES for message encryption.
- Key derived from sender & receiver shared secret.
- Stored data is unreadable without both parties.

---

## **9. Conclusion**
This clean architecture decouples GraphQL, Kafka, Redis, and DB responsibilities. Kafka ensures event-driven scalability. Redis optimizes real-time matching and presence. Sinks deliver low-latency updates to subscribed clients.

🚀 Ready for private, scalable, and reactive social interaction in HappyGigs.
