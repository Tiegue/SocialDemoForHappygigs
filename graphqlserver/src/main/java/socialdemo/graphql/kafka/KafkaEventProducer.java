package socialdemo.graphql.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import socialdemo.graphql.event.UserEnteredEvent;

import static socialdemo.graphql.util.JsonUtil.toJson;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendUserEntered(UserEnteredEvent event) {
        String payload = toJson(event);
        kafkaTemplate.send("user-entered", payload);
    }

    public void sendUserLeft(UserLeftEvent event) {
        String payload = toJson(event);
        kafkaTemplate.send("user-left", payload);
    }

    public void sendUserMessage(ChatMesageEvent event) {
        String payload = toJson(event);
        kafkaTemplate.send("chat-message", payload);
    }
}
