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
The **Social Module** in HappyGigs is an independent microservice that enables **real-time venue-based social interactions** using **GraphQL subscriptions**, **Kafka**, **Redis**, and **PostgreSQL**.

### **Key Features:**
- **Real-time notifications** when users enter/leave a venue.
- **Buzz feature**: Buzz all existing users' devices in a venue when a new user arrives.
- **User list sync** on entry/exit.
- **One-on-one chat** within a venue.
- **Encrypted chat history storage** — retrievable only by both involved users.
- **Integration with Happy module** to fetch venue details.

---

## **2. System Architecture**

### **2.1 Architecture Overview**
```plaintext
                +---------------------+
                |     Happy Module    |
                | (Venue Information) |
                +---------+-----------+
                          |
                          v
               +----------+-----------+
               |      Kafka Topics     |  <--- Async events: userEntered, userLeft, chatMessageSent
               +----------+-----------+
                          |
        +-----------------+-----------------+
        |                                   |
+---------------------+        +------------------------+
|   Social API (GraphQL) |        |   Redis Cache Layer     |
|  - Subscriptions       |        |  - Venue Presence       |
|  - Mutations           |        |  - Message Queues       |
+----------+------------+        +------------------------+
           |
           v
 +--------------------------+
 | VenueTrackerService      |
 | - Business Logic         |
 | - Kafka Producers        |
 | - Per-user Sinks         |
 +-----------+--------------+
             |
             v
    +--------+--------+
    |  PostgreSQL DB   |
    | - Chat Histories |
    | - Encrypted Data |
    +------------------+
```

---

## **3. Data Models**

### **3.1 Message (System Message)**
```java
public class Message {
    private String sender;
    private String receiver;
    private String content; // Notification
    private String timestamp;
}
```

### **3.2 ChatMessageEvent (Private Chat Message)**
```java
public class ChatMessageEvent {
    private String sender;
    private String receiver;
    private String venueId;
    private String content; // Encrypted
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
    private String encryptedMessage;
    private String venueId;
    private long timestamp;
}
```

---

## **4. GraphQL API**

### **4.1 Mutations**
```graphql
type Mutation {
    userEnteredVenue(userId: String!, venueId: String!): String
    userLeftVenue(userId: String!, venueId: String!): String
    sendSystemMessage(sender: String!, receiver: String!, content: String!): String
    sendChatMessage(sender: String!, receiver: String!, venueId: String!, content: String!): String
}
```

### **4.2 Subscriptions**
```graphql
type Subscription {
    receiveMessages(userId: String!): Message
    receiveUserList(userId: String!): [String]
    receiveBuzz(userId: String!): Boolean
    receiveChatMessages(userId: String!): ChatMessageEvent
}
```

---

## **5. Kafka Topics**
- `user-entered`: triggered when a user enters a venue.
- `user-left`: triggered on exit.
- `chat-messages`: one-on-one messages between users.

---

## **6. Redis Usage**
- **Cache presence data**: maintain list of users in venues.
- **Pub/Sub optional**: for internal signaling.

---

## **7. PostgreSQL Usage**
- Store **encrypted** chat history.
- Record user entry/exit logs (optional).

---

## **8. Encryption Strategy**
- Use **AES encryption**.
- Keys derived from user IDs.
- Only sender and receiver can decrypt.

---

## **9. Workflow Scenarios**

### **9.1 New User Enters Venue**
1. Sends `userEnteredVenue` mutation.
2. Backend:
   - Updates Redis presence.
   - Sends Kafka `user-entered` event.
   - Triggers buzz to all existing users.
   - Sends current user list to new user.

### **9.2 User Sends System Message**
1. Sends `sendSystemMessage` mutation.
2. Backend:
   - Broadcasts via Sink.

### **9.3 User Sends Chat Message**
1. Sends `sendChatMessage` mutation.
2. Backend:
   - Encrypts message.
   - Stores to PostgreSQL.
   - Publishes via Kafka `chat-messages`.
   - Delivered via dedicated per-user Sink.

### **9.4 Subscriptions**
- `receiveMessages`: returns system messages.
- `receiveUserList`: returns live user list.
- `receiveBuzz`: UI should buzz the user device.
- `receiveChatMessages`: returns direct messages.

---

## **10. Future Enhancements**
- Integrate with **Keycloak** for identity/auth.
- Deliver media messages (images/audio).
- Venue popularity leaderboard.

---

## **11. Conclusion**
This architecture of the Social Module integrates **Kafka, Redis, and PostgreSQL** for high scalability and data integrity. It offers real-time communication and private interaction within physical venues, forming the foundation for a richer **social layer in HappyGigs**.

🚀 Ready for secure, scalable, and private social experiences!