✅ Start With: Redis & Kafka Integration
Why:
Your core "presence" functionality (buzzing users, syncing lists) depends on Redis for fast in-memory state.

Kafka is now your event backbone — all actions like user entry/exit, messages, and buzz triggers flow through it.

Steps:
Add Kafka & Redis dependencies in graphqlserver/pom.xml.

Configure Redis:

Use it to track users per venue.

Use SetOperations<String, String> to manage user lists.

Configure Kafka:

Define topics: customer-entered, customer-left, chat-messages.

Implement producers to publish entry/exit events.

🔜 Then: Implement PostgreSQL for Encrypted Chat History
Why:
Storage is necessary for message history.

PostgreSQL will hold chat logs in encrypted form.

This part is separate from real-time interaction.

Steps:
Define a ChatMessage JPA entity.

Use AES encryption before persisting messages.

Add repositories for querying messages.

🔁 After That: Connect Everything to GraphQL Resolvers
Wire Redis + Kafka + PostgreSQL logic inside your VenueTrackerService.

Example: when a new user enters, use Redis to get users, use Kafka to notify others, and send subscription updates.

🧪 Parallel: Create Tests and Debug Subscriptions
Run local docker-compose with Redis + Kafka + Postgres.

Test GraphQL with Altair or GraphiQL clients.

Setup subscription listeners in your Next.js frontend.

Bonus Tip:
If you need a sample docker-compose.yml to run Redis + Kafka + Postgres locally, I can generate that for you.

# logs
## 15/05/2025
  - Issue1: Deserialization error: Listener method expects a UserEnteredEvent, but Spring Kafka cannot convert the JSON to Java record.
  - Solution: Use a custom JsonUtils class with a custom deserializer to convert JSON to UserEnteredEvent. 
  - Issue2: Redis Connection RefusedError: Unable to connect to Redis server at 'redis://localhost:6379'
  - Solution: Add a Redis container to your docker-compose.yml.
## 16/05/2025
    - done: 
        📥 Kafka message routing by venueId

        📣 Real-time system messaging via GraphQL Subscriptions

        🔁 Redis-backed presence tracking

        🏷️ Structured MessageType enum for frontend filtering

        🔄 Clean producer-consumer event flow with custom JSON serialization

        📦 Implement PostgreSql with liquibase for history. I know that adding dependencies, liquibase xml files and properties, and then the database created.

    - todo:
        1. Implement PostgreSql for history  
## 19/05/2025
    - done:
        1. Implement PostgreSql for history
        2. Add Liquibase to create the database
        3. Add Liquibase changelog for creating the database
        4. Confirm that frontend and backend are communicating correctly

    - todo:
        1. Operate on the database
        2. Frontend add components
## sprint meetings
    - done:
        1. Show the demo and explain the logic to John, get feedback:
            - Amazing idea for redis to track users per venue;
            - Improve a lot of my skills.
        2. John show happygigs to me:
            - Pretty good UI design;
            - Admin functionality;
        3. Discuss with John:
            - How to deal with the proposals by customers;
            - Fix several bugs: add button, duplicated record of venues while adding a same venue;
    - todo:
        1. Me: 
            - finish postgres for history
            - Add features to frontend
            - Implement keycloak
        2. John: 
            - Fix bugs
            - Add kafka to the project
## 20/05/2025
    - done:
        1. Finish postgres for history. Write to database correctly.
            - Fix Id(UUID) generation, LET JPA generate it. ADD annotation @PrePersist to UserVisitLog entity.
            - Add annotation @Enumerated(EnumType.STRING) to MessageType enum.
        
        2. Add org.slf4j.Logger replace System.out.println.
        3. Frontend and backend are communicating correctly.
    - todo:
        1. Implement keycloak
## 21/05/2025
    - done:
        1. study keycloak
        2. Study the playwrite. 
        3. know the contribute graph of github.( default branch is master, and branch thatmerged to it will be in the graph)
    - todo:
        1. Fix bugs
## 22/05/2025
    - done:
        1. add action but still bugs at kafka, need to find a match version to use zookeeper, as kraftmode is not ok for Actions
        2. learn playwrite. 
        3. 
    - todo:
        1. Fix bugs
## 23/05/2025
    - done:
        1. backen CI done:
            - use binary protocol replace http protocol. KAFKA_CFG_CONTROLLER_QUORUM_VOTERS: "1@kafka:9093" to "1@localhost:9093"
            - Action does support kraft mode, yes.
            - polish it and make it clear, clean and stable
        2. frontend CI done:
            - polish it and make it clear, clean and stable
        3. Configure playwright, and create a demo test, and run it.
    - achieved:
        1. make sense Action and yml file structure.
        2. make sense kafka config.
        3. make sense playwright.
    - todo:
        1. Add real playwright tests for frontend.
        2. learn playwrite.