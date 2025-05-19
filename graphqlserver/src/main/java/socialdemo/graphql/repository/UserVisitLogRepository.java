package socialdemo.graphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import socialdemo.graphql.entity.UserVisitLog;

import java.util.UUID;

@Repository
public interface UserVisitLogRepository extends JpaRepository<UserVisitLog, UUID> {
}
