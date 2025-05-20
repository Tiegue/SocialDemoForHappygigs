package socialdemo.graphql.Mapper;

import socialdemo.graphql.entity.UserVisitLog;
import socialdemo.graphql.event.UserEnteredEvent;
import socialdemo.graphql.event.UserLeftEvent;

import java.sql.Timestamp;
import java.util.UUID;

public class VisitLogMapper  {

    public static UserVisitLog toEntity(UserEnteredEvent record) {
        UserVisitLog userVisitLog = new UserVisitLog();
        //userVisitLog.setId(UUID.randomUUID());
        userVisitLog.setUserId(record.userId());
        userVisitLog.setVenueId(record.venueId());
        userVisitLog.setVisitType(record.visitType());
        userVisitLog.setVisitTime(new Timestamp(record.timestamp()));
        userVisitLog.setSource("");
        return userVisitLog;
    }

    public static UserVisitLog toEntity(UserLeftEvent record) {
        UserVisitLog userVisitLog = new UserVisitLog();
        //userVisitLog.setId(UUID.randomUUID());
        userVisitLog.setUserId(record.userId());
        userVisitLog.setVenueId(record.venueId());
        userVisitLog.setVisitType(record.visitType());
        userVisitLog.setVisitTime(new Timestamp(record.timestamp()));
        userVisitLog.setSource("");
        return userVisitLog;
    }
}
