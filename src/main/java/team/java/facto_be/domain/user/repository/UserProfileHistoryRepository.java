package team.java.facto_be.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.java.facto_be.domain.user.entity.UserProfileHistoryJpaEntity;

import java.util.Optional;

/**
 * 사용자 프로필 변경 이력 Repository.
 */
public interface UserProfileHistoryRepository extends JpaRepository<UserProfileHistoryJpaEntity, Long> {

    /**
     * 특정 사용자의 가장 최근 프로필 변경 이력을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 가장 최근 프로필 변경 이력
     */
    Optional<UserProfileHistoryJpaEntity> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
