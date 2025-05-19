package socialdemo.graphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import socialdemo.graphql.entity.VenueLocation;
import java.util.UUID;

@Repository
public interface VenueLocationRepository extends JpaRepository<VenueLocation, UUID> {
}
