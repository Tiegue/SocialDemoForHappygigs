package socialdemo.graphql.event;

public record UserEnteredEvent (String userId, String venueId, long timestamp){}
