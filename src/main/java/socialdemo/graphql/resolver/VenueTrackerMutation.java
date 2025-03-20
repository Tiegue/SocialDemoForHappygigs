package socialdemo.graphql.resolver;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import socialdemo.graphql.service.VenueTrackerService;

@Controller
public class VenueTrackerMutation {

    private final VenueTrackerService venueTrackerService;

    public VenueTrackerMutation(VenueTrackerService venueTrackerService) {
        this.venueTrackerService = venueTrackerService;
    }

    @MutationMapping
    public String userEnteredVenue(@Argument String userId, @Argument String venueId) {
        venueTrackerService.userEnteredVenue(userId, venueId);
        return "User " + userId + " entered venue " + venueId;
    }

    @MutationMapping("userLeftVenue")
    public String userLeftVenue(@Argument String userId, @Argument String venueId) {
        venueTrackerService.userLeftVenue(userId, venueId);
        return "User " + userId + " left venue " + venueId;
    }
}
