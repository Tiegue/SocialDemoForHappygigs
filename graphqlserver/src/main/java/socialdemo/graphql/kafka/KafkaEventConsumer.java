package socialdemo.graphql.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import socialdemo.graphql.event.ChatMessageEvent;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.model.Message;
import socialdemo.graphql.service.VenueTrackerService;

@Component
public class KafkaEventConsumer {

    private final VenueTrackerService venueTrackerService;

    public KafkaEventConsumer(VenueTrackerService venueTrackerService) {
        this.venueTrackerService = venueTrackerService;
    }

    @KafkaListener(topics = "user-entered", groupId = "social-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserEntered(UserEnteredEvent event) {
        System.out.println("Kafka received user-entered: " + event);

        venueTrackerService.userEnteredVenue(event.getUserId(), event.getVenueId());
    }

    @KafkaListener(topics = "user-left", groupId = "social-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserLeft(UserLeftEvent event) {
        System.out.println("Kafka received user-left: " + event);

        venueTrackerService.userLeftVenue(event.getUserId(), event.getVenueId());
    }

    @KafkaListener(topics = "chat-messages", groupId = "social-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleChatMessage(ChatMessageEvent event) {
        System.out.println("Kafka received chat-message: " + event);

        venueTrackerService.processChatMessage(event);
    }
}

