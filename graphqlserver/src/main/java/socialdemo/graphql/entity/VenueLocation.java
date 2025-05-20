package socialdemo.graphql.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "venue_location")
public class VenueLocation {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "venue_id")
    private String venueId;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "radius_meters")
    private Integer radiusMeters;

    public VenueLocation(String id) {
        this.id = id;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadiusMeters() {
        return radiusMeters;
    }

    public void setRadiusMeters(Integer radiusMeters) {
        this.radiusMeters = radiusMeters;
    }
}
