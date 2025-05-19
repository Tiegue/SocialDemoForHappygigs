package socialdemo.graphql.event;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import socialdemo.graphql.model.VisitType;

public record UserLeftEvent (String userId, String venueId, VisitType visitType, long timestamp){

    private UserLeftEvent(String userId, String venueId, long timestamp) {
        this(userId, venueId, VisitType.LEFT, timestamp);
    }

    public static UserLeftEvent create(String userId, String venueId, long timestamp) {
        return new UserLeftEvent(userId, venueId, timestamp);
    }
}
