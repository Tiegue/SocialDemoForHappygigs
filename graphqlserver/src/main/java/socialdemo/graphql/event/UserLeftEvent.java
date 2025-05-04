package socialdemo.graphql.event;

import org.springframework.boot.autoconfigure.security.SecurityProperties;

public class UserLeftEvent {
    private String userId;
    private String venueId;
    private long timestamp;

    public UserLeftEvent(String userId, String venueId, long timestamp) {
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
