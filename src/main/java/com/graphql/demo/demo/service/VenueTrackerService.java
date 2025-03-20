package social.service;


import social.model.Message;
import social.model.VenuePresence;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.text.SimpleDateFormat;
import java.util.*;

//Notifies only existing users.
//Sends a list of users to the new arrival.
@Service
public class VenueTrackerService {
    private final Map<String, VenuePresence> venueUsers = new HashMap<>();
    private final Sinks.Many<Message> messageSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Set<String>> userListSink = Sinks.many().multicast().onBackpressureBuffer();

    public void userEnteredVenue(String newUserId, String venueId) {
        venueUsers.put(newUserId, new VenuePresence(venueId));
        VenuePresence venue = venueUsers.get(venueId);
        Set<String> existingUsers = venue.getExistingUsers(newUserId);

        // Notify existing users that a new user arrived.
        for (String existingUser : existingUsers) {
            sendMessage(newUserId, existingUser, "A new user: " + newUserId + " entered this venue!");
        }

        // Send the list of existing users to the new user
        Sinks.EmitResult result = userListSink.tryEmitNext(existingUsers);
        printSinkResult(result,"userListSink");


        // Add the new user to the userlist of this venue
        venue.addUser(newUserId);
    }

    public void userLeftVenue(String userId, String venueId) {
        VenuePresence venue = venueUsers.get(venueId);
        if (venue != null) {
            venue.removeUser(userId);
        }
    }
    public void sendMessage(String sender, String receiver, String content) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Sinks.EmitResult result = messageSink.tryEmitNext(new Message(sender, receiver, content, timeStamp));
        printSinkResult(result,"messageSink");
    }

    public Sinks.Many<Message> getMessageSink() {
        return messageSink;
    }
    public Sinks.Many<Set<String>> getUserListSink() {
        return userListSink;
    }

    private void printSinkResult(Sinks.EmitResult result, String content) {
        switch (result) {
            case OK:
                System.out.println(content +": Message sent successfully!");
                break;
            case FAIL_OVERFLOW:
                System.err.println(content +": Backpressure buffer is full!");
                break;
            case FAIL_NON_SERIALIZED:
                System.err.println(content +": Concurrency issue detected!");
                break;
            default:
                System.err.println(content +": Unknown error: " + result);
        }
    }
}

