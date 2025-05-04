package socialdemo.graphql.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

    public static String toIsoString(long epochMillis) {
        return ISO_FORMATTER.format(Instant.ofEpochMilli(epochMillis));
    }

    // Optional: convert back from ISO string to epoch millis
    public static long fromIsoString(String isoTime) {
        return Instant.parse(isoTime).toEpochMilli();
    }
}

