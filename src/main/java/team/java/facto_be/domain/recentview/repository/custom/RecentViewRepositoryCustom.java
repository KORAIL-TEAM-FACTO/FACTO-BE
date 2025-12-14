package team.java.facto_be.domain.recentview.repository.custom;

import team.java.facto_be.domain.recentview.dto.response.TrendingWelfareResponse;
import team.java.facto_be.domain.recentview.entity.RecentViewJpaEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 최근 본 복지 서비스 커스텀 Repository 인터페이스.
 *
 * <p>QueryDSL을 사용한 복잡한 쿼리를 정의합니다.
 */
public interface RecentViewRepositoryCustom {

    /**
     * 사용자의 최근 본 목록을 조회합니다 (limit 포함).
     *
     * @param userId 사용자 ID
     * @param limit 조회 개수
     * @return 최근 본 목록 (최신순)
     */
    List<RecentViewJpaEntity> findByUserIdOrderByViewedAtDesc(Long userId, int limit);

    /**
     * 사용자의 최근 본 목록을 전체 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 최근 본 목록 (최신순)
     */
    List<RecentViewJpaEntity> findByUserIdOrderByViewedAtDesc(Long userId);

    /**
     * 사용자와 복지 서비스 ID로 최근 본 기록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param welfareServiceId 복지 서비스 ID
     * @return 최근 본 기록 (Optional)
     */
    Optional<RecentViewJpaEntity> findByUserIdAndWelfareServiceId(Long userId, String welfareServiceId);

    /**
     * 사용자의 최근 본 기록 개수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 개수
     */
    long countByUserId(Long userId);

    /**
     * 인기 복지 서비스를 집계합니다.
     *
     * <p>최근 N일간 각 복지 서비스를 본 고유 사용자 수를 집계하여
     * 조회수 많은 순으로 정렬합니다.
     *
     * @param since 집계 시작 시점
     * @param limit 조회 개수
     * @return 인기 복지 서비스 목록
     */
    List<TrendingWelfareResponse> findTrendingWelfareServices(LocalDateTime since, int limit);
}
