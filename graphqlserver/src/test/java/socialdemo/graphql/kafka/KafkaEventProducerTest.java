package socialdemo.graphql.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.util.JsonUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaEventProducer kafkaEventProducer;

    @BeforeEach
    void setUp() {
        kafkaEventProducer = new KafkaEventProducer(kafkaTemplate);
    }

    @Test
    void sendUserEntered_ShouldSendEventToKafka() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";
        long timestamp = System.currentTimeMillis();
        UserEnteredEvent event = UserEnteredEvent.create(userId, venueId, timestamp);

        // Act
        kafkaEventProducer.sendUserEntered(event);

        // Assert
        verify(kafkaTemplate).send(eq("user-entered"), eq(event.venueId()), eq(JsonUtils.toJson(event)));
    }

    @Test
    void sendUserLeft_ShouldSendEventToKafka() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";
        long timestamp = System.currentTimeMillis();
        UserLeftEvent event = UserLeftEvent.create(userId, venueId, timestamp);

        // Act
        kafkaEventProducer.sendUserLeft(event);

        // Assert
        verify(kafkaTemplate).send(eq("user-left"), eq(event.venueId()), eq(JsonUtils.toJson(event)));
    }
}