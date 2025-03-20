package social.resolver;

import social.service.VenueTrackerService;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class VenueTrackerMutation {

    private final VenueTrackerService venueTrackerService;

    public VenueTrackerMutation(VenueTrackerService venueTrackerService) {
        this.venueTrackerService = venueTrackerService;
    }

    @MutationMapping("userEnteredVenue")
    public String userEnteredVenue(String newUserId, String venueId) {
        venueTrackerService.userEnteredVenue(newUserId, venueId);
        return "User " + newUserId + " entered venue " + venueId;
    }

    @MutationMapping("userLeftVenue")
    public String userLeftVenue(String userId, String venueId) {
        venueTrackerService.userLeftVenue(userId, venueId);
        return "User " + userId + " left venue " + venueId;
    }
}
