package socialdemo.graphql.event;

import org.springframework.boot.autoconfigure.security.SecurityProperties;

public class UserEnteredEvent {
    private String userId;
    private String venueId;
    private String timestamp;

    public UserEnteredEvent(String userId, String venueId, String timestamp) {
        this.userId = userId;
        this.venueId = venueId;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
