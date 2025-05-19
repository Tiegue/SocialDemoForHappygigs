package socialdemo.graphql.entity;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import socialdemo.graphql.model.VisitType;

import java.util.UUID;
import java.sql.Timestamp;
@Entity
@Table(name = "user_visit_log")
public class UserVisitLog {

    private static final Logger LOG = LoggerFactory.getLogger(UserVisitLog.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "venue_id")
    private UUID venueId;
    @Column(name = "visit_type")
    private VisitType visitType;
    @Column(name = "visit_time")
    private Timestamp visitTime;
    @Column(name = "source")
    private String source;

    public UserVisitLog() {}
    public UserVisitLog(UUID id) {
        this.id = id;

    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getVenueId() { return venueId; }
    public void setVenueId(UUID venueId) { this.venueId = venueId; }
    public VisitType getVisitType() { return visitType; }
    public void setVisitType(VisitType visitType) { this.visitType = visitType; }
    public Timestamp getVisitTime() { return visitTime; }
    public void setVisitTime(Timestamp visitTime) { this.visitTime = visitTime; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
