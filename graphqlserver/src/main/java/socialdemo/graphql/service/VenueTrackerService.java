package socialdemo.graphql.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.kafka.KafkaEventProducer;
import socialdemo.graphql.model.Message;
import socialdemo.graphql.model.MessageType;
import socialdemo.graphql.model.UserListPayload;
import socialdemo.graphql.util.TimeUtils;

import java.util.Set;

@Service
public class VenueTrackerService {

    private final KafkaEventProducer kafkaEventProducer;
    private final StringRedisTemplate redisTemplate;

    private final Sinks.Many<Message> systemMessageSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<UserListPayload> userListSink = Sinks.many().multicast().onBackpressureBuffer();


    public VenueTrackerService(KafkaEventProducer kafkaEventProducer, StringRedisTemplate redisTemplate) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.redisTemplate = redisTemplate;
    }

    // ─────────────────────────────
    // 1. GraphQL Invocation Path
    // ─────────────────────────────
    public void userEnteredVenue(String userId, String venueId) {
        UserEnteredEvent event = new UserEnteredEvent(userId, venueId, System.currentTimeMillis());
        kafkaEventProducer.sendUserEntered(event);
        redisTemplate.opsForSet().add("venue:" + venueId, userId);
    }

    public void userLeftVenue(String userId, String venueId) {
        UserLeftEvent event = new UserLeftEvent(userId, venueId, System.currentTimeMillis());
        kafkaEventProducer.sendUserLeft(event);
        redisTemplate.opsForSet().remove("venue:" + venueId, userId);
    }


    // ─────────────────────────────
    // 2. Kafka Event Handling Path
    // ─────────────────────────────

    public void applyUserEntered(UserEnteredEvent event) {
        // Redis is already updated from mutation for immediate presence, but update again if idempotent
        redisTemplate.opsForSet().add("venue:" + event.venueId(), event.userId());

        // Broadcast to other users in this venue
        Set<String> users = redisTemplate.opsForSet().members("venue:" + event.venueId());
        if (users != null && users.size() >1) {
            for (String currentUser : users) {
                if (!currentUser.equals(event.userId())) {
                    Message msg = new Message(
                            event.userId(),
                            currentUser,
                            "User: " + event.userId() + " entered the venue: " + event.venueId(),
                            TimeUtils.toIsoString(event.timestamp()),
                            MessageType.ENTERED
                    );
                    systemMessageSink.tryEmitNext(msg);
                }
            }

        }

        // Emit user list to new user
        UserListPayload userListPayload = new UserListPayload(event.userId(), users);
        userListSink.tryEmitNext(userListPayload);
    }

    public void applyUserLeft(UserLeftEvent event) {
        redisTemplate.opsForSet().remove("venue:" + event.venueId(), event.userId());

        // Broadcast to other users in this venue
        Set<String> users = redisTemplate.opsForSet().members("venue:" + event.venueId());
        if (users != null && !users.isEmpty()) {
            for (String currentUser : users) {
                if (!currentUser.equals(event.userId())) {
                    Message msg = new Message(
                            event.userId(),
                            currentUser,
                            "User: " + event.userId() + " left the venue: " + event.venueId(),
                            TimeUtils.toIsoString(event.timestamp()),
                            MessageType.LEFT
                    );
                    systemMessageSink.tryEmitNext(msg);
                }
            }
        }

    }

    // ─────────────────────────────
    // 3. GraphQL Subscriptions
    // ─────────────────────────────

    public Sinks.Many<Message> getSystemMessageSink() {
        return systemMessageSink;
    }

    public Sinks.Many<UserListPayload> getUserListSink() {
        return userListSink;
    }


    // For debug
    private void printSinkResult(String sinkType, Sinks.EmitResult result) {
        switch (result) {
            case OK:
                System.out.println(sinkType +": sent successfully!");
                break;
            case FAIL_OVERFLOW:
                System.err.println(sinkType +": Backpressure buffer is full!");
                break;
            case FAIL_NON_SERIALIZED:
                System.err.println(sinkType +": Concurrency issue detected!");
                break;
            default:
                System.err.println(sinkType +": Unknown error: " + result);
        }
    }




}
