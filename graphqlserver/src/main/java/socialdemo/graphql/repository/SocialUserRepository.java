package socialdemo.graphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import socialdemo.graphql.entity.SocialUser;

import java.util.UUID;
@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, UUID> {
}
