package socialdemo.graphql.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import socialdemo.graphql.service.VenueTrackerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VenueTrackerMutationTest {

    @Mock
    private VenueTrackerService venueTrackerService;

    private VenueTrackerMutation venueTrackerMutation;

    @BeforeEach
    void setUp() {
        venueTrackerMutation = new VenueTrackerMutation(venueTrackerService);
    }

    @Test
    void userEnteredVenue_ShouldCallServiceAndReturnMessage() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";
        String expectedMessage = "User " + userId + " entered venue " + venueId;

        // Act
        String result = venueTrackerMutation.userEnteredVenue(userId, venueId);

        // Assert
        verify(venueTrackerService).userEnteredVenue(userId, venueId);
        assertEquals(expectedMessage, result);
    }

    @Test
    void userLeftVenue_ShouldCallServiceAndReturnMessage() {
        // Arrange
        String userId = "user1";
        String venueId = "venue1";
        String expectedMessage = "User " + userId + " left venue " + venueId;

        // Act
        String result = venueTrackerMutation.userLeftVenue(userId, venueId);

        // Assert
        verify(venueTrackerService).userLeftVenue(userId, venueId);
        assertEquals(expectedMessage, result);
    }
}