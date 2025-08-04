package socialdemo.graphql.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.service.VenueTrackerService;
import socialdemo.graphql.util.JsonUtils;

@Component
public class KafkaEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);

    private final VenueTrackerService venueTrackerService;

    public KafkaEventConsumer(VenueTrackerService venueTrackerService) {
        this.venueTrackerService = venueTrackerService;
    }

    @KafkaListener(
            topics = "user-entered",
            groupId = "social-group")
    public void handleUserEntered(String payload, Acknowledgment ack) {//
        try {
            UserEnteredEvent userEnteredEvent = JsonUtils.fromJson(payload, UserEnteredEvent.class);

            venueTrackerService.applyUserEntered(userEnteredEvent);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing user entered event: {}", payload, e);
        }

    }

    @KafkaListener(topics = "user-left",
            groupId = "social-group")
    public void handleUserLeft(String payload) {
        UserLeftEvent userLeftEvent = JsonUtils.fromJson(payload, UserLeftEvent.class);

        venueTrackerService.applyUserLeft(userLeftEvent);
    }

}

