package socialdemo.graphql.event;

import org.springframework.boot.autoconfigure.security.SecurityProperties;

public record UserLeftEvent (String userId, String venueId, long timestamp){}
