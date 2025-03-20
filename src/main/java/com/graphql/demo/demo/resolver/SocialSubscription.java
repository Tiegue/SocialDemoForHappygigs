package social.resolver;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import social.model.Message;
import social.service.VenueTrackerService;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import java.util.Set;

//Handles real-time user notifications and user list updates.
@Controller
public class SocialSubscription {
    private final VenueTrackerService venueTrackerService;

    public SocialSubscription(VenueTrackerService venueTrackerService) {
        this.venueTrackerService = venueTrackerService;
    }

    @SubscriptionMapping
    public Publisher<Message> receiveMessages(String userId) {
        return venueTrackerService.getMessageSink().asFlux()
                .filter(msg -> msg.getReceiver().equals(userId));
    }

    @SubscriptionMapping("receiveUserList")
    public Publisher<Set<String>> receiveUserList(String userId) {
        return venueTrackerService.getUserListSink().asFlux();
    }

    @QueryMapping
    public String test() {
        return "test";
    }

}
