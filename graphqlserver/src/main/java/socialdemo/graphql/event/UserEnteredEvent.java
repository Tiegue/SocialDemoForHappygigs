package socialdemo.graphql.event;

import socialdemo.graphql.model.VisitType;

public record UserEnteredEvent (String userId, String venueId, VisitType visitType, long timestamp)  {

    private UserEnteredEvent(String userId, String venueId, long timestamp) {
        this(userId, venueId, VisitType.ENTERED, timestamp);
    }

    public static UserEnteredEvent create(String userId, String venueId, long timestamp) {
        return new UserEnteredEvent(userId, venueId, timestamp);
    }


}
