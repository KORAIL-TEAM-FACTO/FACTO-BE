package team.java.facto_be.domain.welfare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.welfare.entity.WelfareBookmark;

import java.util.List;
import java.util.Optional;

public interface WelfareBookmarkRepository extends JpaRepository<WelfareBookmark, Long> {

    Optional<WelfareBookmark> findByUserAndServId(UserJpaEntity user, String servId);

    List<WelfareBookmark> findTop100ByUserOrderByCreatedAtDesc(UserJpaEntity user);
}
