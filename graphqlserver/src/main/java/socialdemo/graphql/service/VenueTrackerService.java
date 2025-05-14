package socialdemo.graphql.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Sinks;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.model.Message;

public class VenueTrackerService {

    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final StringRedisTemplate redisTemplate;

    private final Sinks.Many<Message> systemMessageSink = Sinks.many().multicast().onBackpressureBuffer();

    public VenueTrackerService(KafkaTemplate<String,Object> kafkaTemplate, StringRedisTemplate redisTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
    }

    //=== GraphQL Mutatiion entrypoints ===
    public void userEnteredVenue(String userId, String venueId) {
        UserEnteredEvent event = new UserEnteredEvent(userId, venueId, System.currentTimeMillis());
        kafkaTemplate.send("user-entered", event);
        redisTemplate.opsForSet().add("venue:" + venueId, userId);
    }

    public void userEnteredVenue(UserEnteredEvent event) {
        redisTemplate.opsForSet().add("venue:" + event.venueId(), event.userId());
        // Also store event to PostgreSQL as visit log
    }

    public void userLeftVenue(String userId, String venueId) {
        kafkaTemplate.send("user-left", new UserLeftEvent(userId, venueId));
    }
}
