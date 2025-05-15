package socialdemo.graphql.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeUtilsTest {

    @Test
    void toIsoString_ShouldConvertEpochMillisToIsoString() {
        // Arrange
        long epochMillis = 1609459200000L; // 2021-01-01T00:00:00Z

        // Act
        String result = TimeUtils.toIsoString(epochMillis);

        // Assert
        assertEquals("2021-01-01T00:00:00Z", result);
    }

    @Test
    void fromIsoString_ShouldConvertIsoStringToEpochMillis() {
        // Arrange
        String isoTime = "2021-01-01T00:00:00Z";
        long expectedEpochMillis = 1609459200000L;

        // Act
        long result = TimeUtils.fromIsoString(isoTime);

        // Assert
        assertEquals(expectedEpochMillis, result);
    }

    @Test
    void roundTrip_ShouldPreserveOriginalValue() {
        // Arrange
        long originalEpochMillis = 1609459200000L; // 2021-01-01T00:00:00Z

        // Act
        String isoString = TimeUtils.toIsoString(originalEpochMillis);
        long roundTripEpochMillis = TimeUtils.fromIsoString(isoString);

        // Assert
        assertEquals(originalEpochMillis, roundTripEpochMillis);
    }
}