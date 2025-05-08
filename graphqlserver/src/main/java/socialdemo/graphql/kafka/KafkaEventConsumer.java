package socialdemo.graphql.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import socialdemo.graphql.event.ChatMessageEvent;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.model.Message;
import socialdemo.graphql.service.VenueTrackerService;
import socialdemo.graphql.util.JsonUtils;

@Component
public class KafkaEventConsumer {

    private final VenueTrackerService venueTrackerService;

    public KafkaEventConsumer(VenueTrackerService venueTrackerService) {
        this.venueTrackerService = venueTrackerService;
    }

    @KafkaListener(topics = "user-entered", groupId = "social-group")
    public void handleUserEntered(String payload) {
        UserEnteredEvent userEnteredEvent = JsonUtils.fromJson(payload, UserEnteredEvent.class);

        venueTrackerService.userEnteredVenue(userEnteredEvent.getUserId(), userEnteredEvent.getVenueId());
    }

    @KafkaListener(topics = "user-left", groupId = "social-group")
    public void handleUserLeft(String payload) {
        UserLeftEvent userLeftEvent = JsonUtils.fromJson(payload, UserLeftEvent.class);

        venueTrackerService.userLeftVenue(userLeftEvent.getUserId(), userLeftEvent.getVenueId());
    }

    @KafkaListener(topics = "chat-message", groupId = "social-group")
    public void handleChatMessage(String payload) {
        ChatMessageEvent chatMessageEvent = JsonUtils.fromJson(payload, ChatMessageEvent.class);

        venueTrackerService.sendPrivateChat(chatMessageEvent);
    }
}

