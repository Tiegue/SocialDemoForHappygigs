package socialdemo.graphql.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;
import socialdemo.graphql.Mapper.VisitLogMapper;
import socialdemo.graphql.entity.UserVisitLog;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;
import socialdemo.graphql.kafka.KafkaEventProducer;
import socialdemo.graphql.model.Message;
import socialdemo.graphql.model.VisitType;
import socialdemo.graphql.model.UserListPayload;
import socialdemo.graphql.repository.UserVisitLogRepository;
import socialdemo.graphql.util.TimeUtils;

import java.util.Set;

@Service
public class VenueTrackerService {

    private static final Logger log = LoggerFactory.getLogger(VenueTrackerService.class);

    private final KafkaEventProducer kafkaEventProducer;
    private final StringRedisTemplate redisTemplate;

    private final Sinks.Many<Message> systemMessageSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<UserListPayload> userListSink = Sinks.many().multicast().onBackpressureBuffer();
    private final UserVisitLogRepository userVisitLogRepository;


    public VenueTrackerService(KafkaEventProducer kafkaEventProducer, StringRedisTemplate redisTemplate, UserVisitLogRepository userVisitLogRepository) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.redisTemplate = redisTemplate;
        this.userVisitLogRepository = userVisitLogRepository;
    }

    // ─────────────────────────────
    // 1. GraphQL Invocation Path
    // ─────────────────────────────
    public void userEnteredVenue(String userId, String venueId) {
        UserEnteredEvent event = UserEnteredEvent.create(userId, venueId, System.currentTimeMillis());
        kafkaEventProducer.sendUserEntered(event);
        redisTemplate.opsForSet().add("venue:" + venueId, userId);
    }

    public void userLeftVenue(String userId, String venueId) {
        UserLeftEvent event = UserLeftEvent.create(userId, venueId, System.currentTimeMillis());
        kafkaEventProducer.sendUserLeft(event);
        redisTemplate.opsForSet().remove("venue:" + venueId, userId);
    }


    // ─────────────────────────────
    // 2. Kafka Event Handling Path
    // ─────────────────────────────

    public void applyUserEntered(UserEnteredEvent event) {
        // Redis is already updated from mutation for immediate presence, but update again if idempotent
        redisTemplate.opsForSet().add("venue:" + event.venueId(), event.userId());

        // Broadcast to other users in this venue
        Set<String> users = redisTemplate.opsForSet().members("venue:" + event.venueId());
        if (users != null && users.size() >1) {
            for (String currentUser : users) {
                if (!currentUser.equals(event.userId())) {
                    Message msg = new Message(
                            event.userId(),
                            currentUser,
                            "User: " + event.userId() + " entered the venue: " + event.venueId(),
                            TimeUtils.toIsoString(event.timestamp()),
                            VisitType.ENTERED
                    );
                    log.debug("check message: {}", msg.toString());
                    Sinks.EmitResult result = systemMessageSink.tryEmitNext(msg);
                    logSinkResult("systemMessageSink", result);
                }
            }

        }

        // Emit user list to new user
        UserListPayload userListPayload = new UserListPayload(event.userId(), users);
        log.debug("check userListPayload: {}", userListPayload.toString());
        Sinks.EmitResult result = userListSink.tryEmitNext(userListPayload);
        logSinkResult("userListSink", result);

        //Persist to postgres
        UserVisitLog vistlog = VisitLogMapper.toEntity(event);
        userVisitLogRepository.save(vistlog);
    }

    public void applyUserLeft(UserLeftEvent event) {
        redisTemplate.opsForSet().remove("venue:" + event.venueId(), event.userId());

        // Broadcast to other users in this venue
        Set<String> users = redisTemplate.opsForSet().members("venue:" + event.venueId());
        if (users != null && !users.isEmpty()) {
            for (String currentUser : users) {
                if (!currentUser.equals(event.userId())) {
                    Message msg = new Message(
                            event.userId(),
                            currentUser,
                            "User: " + event.userId() + " left the venue: " + event.venueId(),
                            TimeUtils.toIsoString(event.timestamp()),
                            VisitType.LEFT
                    );
                    log.debug("check message: {}", msg.toString());
                    Sinks.EmitResult result =systemMessageSink.tryEmitNext(msg);
                    logSinkResult("systemMessageSink", result);
                }
            }
        }

        //Persist to postgres
        UserVisitLog vistlog = VisitLogMapper.toEntity(event);
        userVisitLogRepository.save(vistlog);

    }

    // ─────────────────────────────
    // 3. GraphQL Subscriptions
    // ─────────────────────────────

    public Sinks.Many<Message> getSystemMessageSink() {
        return systemMessageSink;
    }

    public Sinks.Many<UserListPayload> getUserListSink() {
        return userListSink;
    }


    // For debug
    private void logSinkResult(String sinkType, Sinks.EmitResult result) {
        switch (result) {
            case OK:
                log.info("{}: sent successfully!", sinkType);
                break;
            case FAIL_OVERFLOW:
                log.info("{}: Backpressure buffer is full!", sinkType);
                break;
            case FAIL_NON_SERIALIZED:
                log.info("{}: Sink is not serializable!", sinkType);
                break;
            default:
                log.info("{}: Unknown error.", sinkType);
        }
    }




}
