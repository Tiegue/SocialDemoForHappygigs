package socialdemo.graphql.model;

import java.util.UUID;

public class UserVenueVisitingLog {
    private UUID id;
    private String userId;
    private String venueId;
    private String action; // "ENTERED" or "LEFT"
    private long timestamp;
}
