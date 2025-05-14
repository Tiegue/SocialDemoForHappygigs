package socialdemo.graphql.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import socialdemo.graphql.event.ChatMessageEvent;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.service.VenueTrackerService;
import socialdemo.graphql.util.JsonUtils;

@Component
public class KafkaEventConsumer {

    private final VenueTrackerService venueTrackerService;

    public KafkaEventConsumer(VenueTrackerService venueTrackerService) {
        this.venueTrackerService = venueTrackerService;
    }

    @KafkaListener(topics = "user-entered", groupId = "social-group")
    public void handleUserEntered(UserEnteredEvent event) {
        UserEnteredEvent userEnteredEvent = JsonUtils.fromJson(payload, UserEnteredEvent.class);

        venueTrackerService.userEnteredVenue(event);
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

