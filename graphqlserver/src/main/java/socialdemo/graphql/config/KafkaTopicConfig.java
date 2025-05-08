package socialdemo.graphql.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaTopicConfig {

    @Bean
    public NewTopic userEnteredTopic() {
        return TopicBuilder.name("user-entered")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userLeftTopic() {
        return TopicBuilder.name("user-left")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userMessageTopic() {
        return TopicBuilder.name("chat-message")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
