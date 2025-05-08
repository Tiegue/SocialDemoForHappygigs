package socialdemo.graphql.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import socialdemo.graphql.event.ChatMessageEvent;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;

import static socialdemo.graphql.util.JsonUtils.toJson;

@Service
public class KafkaEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserEntered(UserEnteredEvent event) {
        String payload = toJson(event);
        kafkaTemplate.send("user-entered", payload);
    }

    public void sendUserLeft(UserLeftEvent event) {
        String payload = toJson(event);
        kafkaTemplate.send("user-left", payload);
    }

    public void sendUserMessage(ChatMessageEvent event) {
        String payload = toJson(event);
        kafkaTemplate.send("chat-message", payload);
    }
}
