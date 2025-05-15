package socialdemo.graphql.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.service.VenueTrackerService;
import socialdemo.graphql.util.JsonUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaEventConsumerTest {

    @Mock
    private VenueTrackerService venueTrackerService;

    private KafkaEventConsumer kafkaEventConsumer;

    @BeforeEach
    void setUp() {
        kafkaEventConsumer = new KafkaEventConsumer(venueTrackerService);
    }

    @Test
    void handleUserEntered_ShouldCallVenueTrackerService() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";
        long timestamp = System.currentTimeMillis();
        UserEnteredEvent event = new UserEnteredEvent(userId, venueId, timestamp);
        String payload = JsonUtils.toJson(event);

        // Act
        kafkaEventConsumer.handleUserEntered(payload);

        // Assert
        verify(venueTrackerService).applyUserEntered(event);
    }

    @Test
    void handleUserLeft_ShouldCallVenueTrackerService() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";
        long timestamp = System.currentTimeMillis();
        UserLeftEvent event = new UserLeftEvent(userId, venueId, timestamp);
        String payload = JsonUtils.toJson(event);

        // Act
        kafkaEventConsumer.handleUserLeft(payload);

        // Assert
        verify(venueTrackerService).applyUserLeft(event);
    }
}