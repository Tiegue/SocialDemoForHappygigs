package com.happygigs.social.health;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private final RedisConnectionFactory redisConnectionFactory;
    private final KafkaAdmin kafkaAdmin;

    @Autowired
    public CustomHealthIndicator(
            JdbcTemplate jdbcTemplate,
            RedisConnectionFactory redisConnectionFactory,
            KafkaAdmin kafkaAdmin
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisConnectionFactory = redisConnectionFactory;
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public Health health() {
        Health.Builder builder = Health.up();

        // Check database
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            builder.withDetail("database", "available");
        } catch (Exception e) {
            builder.down().withDetail("database", "unavailable: " + e.getMessage());
        }

        // Check Redis
        try {
            redisConnectionFactory.getConnection().ping();
            builder.withDetail("redis", "available");
        } catch (Exception e) {
            builder.down().withDetail("redis", "unavailable: " + e.getMessage());
        }

        // Check Kafka
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DescribeClusterResult cluster = adminClient.describeCluster();
            KafkaFuture<String> future = cluster.clusterId();
            future.get(3, TimeUnit.SECONDS); // Wait max 3s
            builder.withDetail("kafka", "available");
        } catch (Exception e) {
            builder.down().withDetail("kafka", "unavailable: " + e.getMessage());
        }

        return builder.build();
    }
}
