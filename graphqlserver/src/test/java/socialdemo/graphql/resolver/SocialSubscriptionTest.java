package socialdemo.graphql.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import socialdemo.graphql.model.Message;
import socialdemo.graphql.model.UserListPayload;
import socialdemo.graphql.service.VenueTrackerService;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SocialSubscriptionTest {

    @Mock
    private VenueTrackerService venueTrackerService;

    @Mock
    private Sinks.Many<Message> messageSink;

    @Mock
    private Sinks.Many<UserListPayload> userListSink;

    private SocialSubscription socialSubscription;

    @BeforeEach
    void setUp() {
        socialSubscription = new SocialSubscription(venueTrackerService);
    }

    @Test
    void receiveMessages_ShouldFilterMessagesByUserId() {
        // Arrange
        String userId = "user1";
        Message message1 = new Message("sender1", userId, "Hello user1", "2023-01-01T12:00:00Z");
        Message message2 = new Message("sender2", "user2", "Hello user2", "2023-01-01T12:00:00Z");
        
        Flux<Message> messageFlux = Flux.just(message1, message2);
        
        when(venueTrackerService.getSystemMessageSink()).thenReturn(messageSink);
        when(messageSink.asFlux()).thenReturn(messageFlux);

        // Act
        Publisher<Message> result = socialSubscription.receiveMessages(userId);

        // Assert
        StepVerifier.create(result)
                .expectNext(message1)
                .verifyComplete();
    }

    @Test
    void receiveUserList_ShouldFilterUserListByUserId() {
        // Arrange
        String userId = "user1";
        Set<String> users1 = new HashSet<>();
        users1.add("user1");
        users1.add("user2");
        
        Set<String> users2 = new HashSet<>();
        users2.add("user3");
        users2.add("user4");
        
        UserListPayload payload1 = new UserListPayload(userId, users1);
        UserListPayload payload2 = new UserListPayload("user2", users2);
        
        Flux<UserListPayload> userListFlux = Flux.just(payload1, payload2);
        
        when(venueTrackerService.getUserListSink()).thenReturn(userListSink);
        when(userListSink.asFlux()).thenReturn(userListFlux);

        // Act
        Publisher<Set<String>> result = socialSubscription.receiveUserList(userId);

        // Assert
        StepVerifier.create(result)
                .expectNext(users1)
                .verifyComplete();
    }

    @Test
    void test_ShouldReturnTestString() {
        // Act
        String result = socialSubscription.test();
        
        // Assert
        assertEquals("test", result);
    }
}