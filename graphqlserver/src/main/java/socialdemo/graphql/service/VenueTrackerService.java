package socialdemo.graphql.service;


import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;
import socialdemo.graphql.model.Message;
import socialdemo.graphql.model.UserListPayload;
import socialdemo.graphql.model.VenuePresence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//Notifies only existing users.
//Sends a list of users to the new arrival.
@Service
public class VenueTrackerService {
    private final Map<String, VenuePresence> venueUsers = new HashMap<>();
    //Scenarios where only live users need to see the event
    private final Sinks.Many<Message> messageSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<UserListPayload> userListSink = Sinks.many().multicast().onBackpressureBuffer();

    //private final Map<String, Sinks.Many<Set<String>>> userListSinks = new ConcurrentHashMap<>();
    //private final Sinks.Many<Set<String>> userListSinks = Sinks.many().multicast().onBackpressureBuffer();
    //Any subscriber that connects later immediately receives the last value
//    private final Sinks.Many<Message> messageSink = Sinks.many().replay().limit(1);
//    private final Sinks.Many<Set<String>> userListSink = Sinks.many().replay().limit(1);


    public void userEnteredVenue(String newUserId, String venueId) {
        venueUsers.computeIfAbsent(venueId, VenuePresence::new);
        VenuePresence venue = venueUsers.get(venueId);
        Set<String> existingUsers = venue.getExistingUsers(newUserId);
        System.out.println(existingUsers);

        // Notify existing users that a new user arrived.
        for (String existingUser : existingUsers) {
            Sinks.EmitResult result = sendMessage(newUserId, existingUser, "A new user: " + newUserId + " entered this venue!");

            // Check the result for debug.
            printSinkResult("Message", result);
            System.out.println("New user: " + newUserId + " entered this venue-" + venueId + " which has : " + existingUser);
        }

        // Send the list of existing users in this venue to the new user
        Sinks.EmitResult result = sendUserList(newUserId, existingUsers);

        // Check the result for debug.
        printSinkResult("userListSink in " + venueId, result);


        // Add the new user to the userlist of this venue
        venue.addUser(newUserId);
    }

    public void userLeftVenue(String userId, String venueId) {
        VenuePresence venue = venueUsers.get(venueId);
        if (venue != null) {
            venue.removeUser(userId);
        }
    }
    public Sinks.EmitResult sendMessage(String sender, String receiver, String content) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return messageSink.tryEmitNext(new Message(sender, receiver, content, timeStamp));

    }
    public Sinks.Many<Message> getMessageSink() {
        return messageSink;
    }

    public Sinks.EmitResult sendUserList(String newUserId, Set<String> existingUsers) {
        return userListSink.tryEmitNext(new UserListPayload(newUserId, existingUsers));
    }
    public Sinks.Many<UserListPayload> getUserListSink() {
        return userListSink;
    }

    // For debug
    private void printSinkResult(String sinkType, Sinks.EmitResult result) {
        switch (result) {
            case OK:
                System.out.println(sinkType +": sent successfully!");
                break;
            case FAIL_OVERFLOW:
                System.err.println(sinkType +": Backpressure buffer is full!");
                break;
            case FAIL_NON_SERIALIZED:
                System.err.println(sinkType +": Concurrency issue detected!");
                break;
            default:
                System.err.println(sinkType +": Unknown error: " + result);
        }
    }
}

