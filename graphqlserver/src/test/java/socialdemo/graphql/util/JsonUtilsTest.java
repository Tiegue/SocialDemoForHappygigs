package socialdemo.graphql.util;

import org.junit.jupiter.api.Test;
import socialdemo.graphql.event.UserEnteredEvent;

import static org.junit.jupiter.api.Assertions.*;

public class JsonUtilsTest {

    @Test
    void toJson_ShouldSerializeObjectToJsonString() {
        // Arrange
        UserEnteredEvent event = UserEnteredEvent.create("user1", "venue1", 1609459200000L);

        // Act
        String json = JsonUtils.toJson(event);

        // Assert
        assertTrue(json.contains("\"userId\":\"user1\""));
        assertTrue(json.contains("\"venueId\":\"venue1\""));
        assertTrue(json.contains("\"timestamp\":1609459200000"));
    }

    @Test
    void fromJson_ShouldDeserializeJsonStringToObject() {
        // Arrange
        String json = "{\"userId\":\"user1\",\"venueId\":\"venue1\",\"timestamp\":1609459200000}";

        // Act
        UserEnteredEvent event = JsonUtils.fromJson(json, UserEnteredEvent.class);

        // Assert
        assertEquals("user1", event.userId());
        assertEquals("venue1", event.venueId());
        assertEquals(1609459200000L, event.timestamp());
    }

    @Test
    void toJson_ShouldThrowRuntimeExceptionWhenSerializationFails() {
        // Arrange
        Object circularReference = new Object() {
            @Override
            public String toString() {
                return "Unserializable object";
            }
        };

        // Act & Assert
        assertThrows(RuntimeException.class, () -> JsonUtils.toJson(circularReference));
    }

    @Test
    void fromJson_ShouldThrowRuntimeExceptionWhenDeserializationFails() {
        // Arrange
        String invalidJson = "{invalid json}";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> JsonUtils.fromJson(invalidJson, UserEnteredEvent.class));
    }

    @Test
    void roundTrip_ShouldPreserveOriginalObject() {
        // Arrange
        UserEnteredEvent originalEvent = UserEnteredEvent.create("user1", "venue1", 1609459200000L);

        // Act
        String json = JsonUtils.toJson(originalEvent);
        UserEnteredEvent deserializedEvent = JsonUtils.fromJson(json, UserEnteredEvent.class);

        // Assert
        assertEquals(originalEvent.userId(), deserializedEvent.userId());
        assertEquals(originalEvent.venueId(), deserializedEvent.venueId());
        assertEquals(originalEvent.timestamp(), deserializedEvent.timestamp());
    }
}