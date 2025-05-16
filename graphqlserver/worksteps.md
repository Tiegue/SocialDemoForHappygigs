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