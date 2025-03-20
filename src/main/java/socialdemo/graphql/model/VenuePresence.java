package socialdemo.graphql.model;

import java.util.HashSet;
import java.util.Set;

public class VenuePresence {
    private String venueId;
    private Set<String> users;

    public VenuePresence(String venueId) {
        this.venueId = venueId;
        this.users = new HashSet<>();
    }

    public String getVenueId() {
        return venueId;
    }
    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public void addUser(String userId){
        users.add(userId);
    }
    public void removeUser(String userId) {
        users.remove(userId);
    }
    public void removeAllUsers() {
        users.clear();
    }
    public boolean hasMultipleUsers() {
        return users.size() > 1;
    }
    public boolean hasUsers() {
        return !users.isEmpty();
    }
    // Returns a list of existing users when a new user arrives. Call this method before sending message.
    public Set<String> getExistingUsers(String newUserId) {
        Set<String> existingUsers = new HashSet<>(users);
        existingUsers.remove(newUserId);
        return existingUsers;
    }
}
