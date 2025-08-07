package socialdemo.graphql.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static socialdemo.graphql.util.JsonUtils.objectMapper;
import static socialdemo.graphql.util.JsonUtils.toJson;

@Service
public class KafkaEventProducer {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KafkaEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

//    public void sendUserEntered(UserEnteredEvent event) {
//        String payload = toJson(event);
//        kafkaTemplate.send("user-entered", event.venueId(), payload);
//    }

    public void sendUserLeft(UserLeftEvent event) {
        String payload = toJson(event);
        kafkaTemplate.send("user-left", event.venueId(), payload);
    }

    public void sendUserEntered(UserEnteredEvent event) {
        try {
            // Serialize event to JSON
            String payload = toJson(event);
            String topic = "user-entered";
            String key = event.venueId();

            // Create producer record
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, payload);

            // Send message asynchronously with callback
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    // Success
                    logger.info("Successfully sent message to topic {} with key {} and offset {}",
                            topic, key, result.getRecordMetadata().offset());
                } else {
                    // Handle failure
                    logger.error("Failed to send message to topic {} with key {}: {}",
                            topic, key, ex.getMessage());
                    handleSendFailure(event, ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error processing event for topic user-entered with venueId {}: {}",
                    event.venueId(), e.getMessage());
            throw new KafkaException("Failed to send UserEnteredEvent", e);
        }
    }

    private void handleSendFailure(UserEnteredEvent event, Throwable ex) {
        try {
            // Create a DLQ message with additional metadata
            Map<String, Object> dlqMessage = new HashMap<>();
            dlqMessage.put("originalEvent", event);
            dlqMessage.put("errorMessage", ex.getMessage());
            dlqMessage.put("timestamp", Instant.now().toString());
            dlqMessage.put("originalTopic", "user-entered");

            // Serialize DLQ message
            String dlqPayload = objectMapper.writeValueAsString(dlqMessage);
            String key = event.venueId();

            // Send to DLQ topic
            ProducerRecord<String, String> dlqRecord = new ProducerRecord<>(DLQ_TOPIC, key, dlqPayload);
            kafkaTemplate.send(dlqRecord).whenComplete((dlqResult, dlqEx) -> {
                if (dlqEx == null) {
                    logger.info("Successfully sent message to DLQ topic {} with key {} and offset {}",
                            DLQ_TOPIC, key, dlqResult.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send message to DLQ topic {} with key {}: {}",
                            DLQ_TOPIC, key, dlqEx.getMessage());
                    // Fallback: Log to file, database, or alert system
                    logger.warn("Consider storing failed DLQ message in alternative storage for venueId {}", key);
                }
            });
        } catch (Exception dlqEx) {
            logger.error("Failed to send message to DLQ for venueId {}: {}",
                    event.venueId(), dlqEx.getMessage());
            // Fallback: Log to file, database, or alert system
            logger.warn("Critical: Unable to send to DLQ, consider alternative storage for venueId {}", event.venueId());
        }
    }

}
