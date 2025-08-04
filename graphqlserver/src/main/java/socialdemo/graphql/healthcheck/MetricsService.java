package socialdemo.graphql.healthcheck;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> venueCounters = new ConcurrentHashMap<>();

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordUserEntered(String venueId) {
        venueCounters
                .computeIfAbsent(venueId, id ->
                        Counter.builder("user.entered.venue")
                                .description("Number of users who entered each venue")
                                .tags("venue", id)
                                .register(meterRegistry)
                )
                .increment();
    }
}
