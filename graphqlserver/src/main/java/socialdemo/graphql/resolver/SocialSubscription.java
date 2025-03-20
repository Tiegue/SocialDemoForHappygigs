package socialdemo.graphql.resolver;

import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import socialdemo.graphql.model.Message;
import socialdemo.graphql.service.VenueTrackerService;

import java.util.Set;

//Handles real-time user notifications and user list updates.
@Controller
public class SocialSubscription {
    private final VenueTrackerService venueTrackerService;

    public SocialSubscription(VenueTrackerService venueTrackerService) {
        this.venueTrackerService = venueTrackerService;
    }

    @SubscriptionMapping
    public Publisher<Message> receiveMessages(@Argument String userId) {
        return venueTrackerService.getMessageSink().asFlux()
                .filter(msg -> msg.getReceiver().equals(userId));
    }

    @SubscriptionMapping
    public Publisher<Set<String>> receiveUserList(@Argument String userId) {
        return venueTrackerService.getUserListSink().asFlux();
    }

    @QueryMapping
    public String test() {
        return "test";
    }

}
