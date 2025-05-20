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
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue
    private UUID id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "venue_id")
    private String venueId;
    @Column(name = "visit_type")
    @Enumerated(EnumType.STRING)
    private VisitType visitType;
    @Column(name = "visit_time")
    private Timestamp visitTime;
    @Column(name = "source")
    private String source;

    public UserVisitLog() {}
    @PrePersist
    public void perPersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }
    public VisitType getVisitType() { return visitType; }
    public void setVisitType(VisitType visitType) { this.visitType = visitType; }
    public Timestamp getVisitTime() { return visitTime; }
    public void setVisitTime(Timestamp visitTime) { this.visitTime = visitTime; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
