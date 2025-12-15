package team.java.facto_be.domain.welfare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.java.facto_be.domain.welfare.entity.WelfareViewHistoryJpaEntity;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 복지 서비스 조회 이력 Repository.
 */
public interface WelfareViewHistoryRepository extends JpaRepository<WelfareViewHistoryJpaEntity, Long> {

    /**
     * 특정 복지 서비스의 총 조회수를 조회합니다.
     *
     * @param serviceId 복지 서비스 ID
     * @return 총 조회수
     */
    long countByServiceId(String serviceId);

    /**
     * 특정 복지 서비스의 고유 사용자 수를 조회합니다 (중복 제거).
     *
     * @param serviceId 복지 서비스 ID
     * @return 고유 사용자 수
     */
    @Query("SELECT COUNT(DISTINCT vh.userId) FROM WelfareViewHistoryJpaEntity vh WHERE vh.serviceId = :serviceId AND vh.userId IS NOT NULL")
    long countDistinctUsersByServiceId(@Param("serviceId") String serviceId);

    /**
     * 특정 사용자가 특정 복지 서비스를 최근에 조회한 이력이 있는지 확인합니다.
     * 중복 조회 방지를 위해 사용됩니다 (24시간 기준).
     *
     * @param userId 사용자 ID
     * @param serviceId 복지 서비스 ID
     * @param since 기준 시간 (예: 24시간 전)
     * @return 최근 조회 이력
     */
    Optional<WelfareViewHistoryJpaEntity> findFirstByUserIdAndServiceIdAndViewedAtAfterOrderByViewedAtDesc(
            Long userId,
            String serviceId,
            LocalDateTime since
    );

    /**
     * 특정 기간 동안 가장 많이 조회된 복지 서비스 TOP N을 조회합니다.
     *
     * @param since 기준 시간
     * @param limit 조회 개수
     * @return 서비스 ID와 조회수 목록
     */
    @Query("SELECT vh.serviceId, COUNT(vh) as viewCount " +
           "FROM WelfareViewHistoryJpaEntity vh " +
           "WHERE vh.viewedAt >= :since " +
           "GROUP BY vh.serviceId " +
           "ORDER BY viewCount DESC")
    java.util.List<Object[]> findTopViewedServices(@Param("since") LocalDateTime since,
                                                     org.springframework.data.domain.Pageable pageable);
}
