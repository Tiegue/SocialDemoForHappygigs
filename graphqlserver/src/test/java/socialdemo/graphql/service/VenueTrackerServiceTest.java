package socialdemo.graphql.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.healthcheck.MetricsService;
import socialdemo.graphql.kafka.KafkaEventProducer;
import socialdemo.graphql.model.Message;
import socialdemo.graphql.model.UserListPayload;
import socialdemo.graphql.repository.UserVisitLogRepository;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VenueTrackerServiceTest {

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private SetOperations<String, String> setOperations;

    private VenueTrackerService venueTrackerService;

    @Mock
    private UserVisitLogRepository userVisitLogRepository;

    @Mock
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);
        venueTrackerService = new VenueTrackerService(kafkaEventProducer, redisTemplate, userVisitLogRepository, metricsService);
    }

    @Test
    void userEnteredVenue_ShouldSendEventAndUpdateRedis() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";

        // Act
        venueTrackerService.userEnteredVenue(userId, venueId);

        // Assert
        verify(kafkaEventProducer).sendUserEntered(any(UserEnteredEvent.class));
        verify(setOperations).add(eq("venue:" + venueId), eq(userId));
    }

    @Test
    void userLeftVenue_ShouldSendEventAndUpdateRedis() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";

        // Act
        venueTrackerService.userLeftVenue(userId, venueId);

        // Assert
        verify(kafkaEventProducer).sendUserLeft(any(UserLeftEvent.class));
        verify(setOperations).remove(eq("venue:" + venueId), eq(userId));
    }

    @Test
    void applyUserEntered_ShouldUpdateRedisAndEmitMessages() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";
        UserEnteredEvent event = UserEnteredEvent.create(userId, venueId, System.currentTimeMillis());

        Set<String> users = new HashSet<>();
        users.add(userId);
        users.add("user2");

        when(setOperations.members(anyString())).thenReturn(users);

        // Act
        venueTrackerService.applyUserEntered(event);

        // Assert
        verify(setOperations).add(eq("venue:" + venueId), eq(userId));
        verify(setOperations).members(eq("venue:" + venueId));

        // Test that the sink emits the expected message
        Flux<Message> messageFlux = venueTrackerService.getSystemMessageSink().asFlux();
        StepVerifier.create(messageFlux.take(1))
                .expectNextMatches(msg -> 
                    msg.sender().equals(userId) &&
                    msg.receiver().equals("user2") &&
                    msg.content().contains(userId) &&
                    msg.content().contains(venueId))
                .verifyComplete();

        // Test that the user list sink emits the expected payload
        Flux<UserListPayload> userListFlux = venueTrackerService.getUserListSink().asFlux();
        StepVerifier.create(userListFlux.take(1))
                .expectNextMatches(payload -> 
                    payload.userId().equals(userId) && 
                    payload.users().equals(users))
                .verifyComplete();
    }

    @Test
    void applyUserLeft_ShouldUpdateRedis() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";
        UserLeftEvent event = UserLeftEvent.create(userId, venueId, System.currentTimeMillis());

        // Act
        venueTrackerService.applyUserLeft(event);

        // Assert
        verify(setOperations).remove(eq("venue:" + venueId), eq(userId));
    }

    @Test
    void getSystemMessageSink_ShouldReturnNonNullSink() {
        // Act
        Sinks.Many<Message> sink = venueTrackerService.getSystemMessageSink();

        // Assert
        assertNotNull(sink);
    }

    @Test
    void getUserListSink_ShouldReturnNonNullSink() {
        // Act
        Sinks.Many<UserListPayload> sink = venueTrackerService.getUserListSink();

        // Assert
        assertNotNull(sink);
    }
}
