package team.java.facto_be.domain.recentview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.recentview.dto.response.RecentViewResponse;
import team.java.facto_be.domain.recentview.dto.response.TrendingWelfareResponse;
import team.java.facto_be.domain.recentview.entity.RecentViewJpaEntity;
import team.java.facto_be.domain.recentview.repository.RecentViewRepository;
import team.java.facto_be.domain.user.entity.UserJpaEntity;
import team.java.facto_be.domain.user.facade.UserFacade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 최근 본 복지 서비스 비즈니스 로직 서비스.
 *
 * <p>최근 본 복지 서비스 추가, 조회, 인기 복지 서비스 TOP 10 집계 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class RecentViewService {

    /** 사용자당 최대 저장 가능한 최근 본 기록 수 */
    private static final int MAX_RECENT_VIEWS = 100;

    /** 인기 복지 서비스 집계 기본 기간 (일) */
    private static final int DEFAULT_TRENDING_DAYS = 7;

    private final RecentViewRepository recentViewRepository;
    private final UserFacade userFacade;

    /**
     * 복지 서비스를 최근 본 목록에 추가합니다.
     *
     * <p>동작 흐름:
     * 1. JWT 토큰에서 현재 로그인한 사용자 정보 추출
     * 2. 이미 본 적 있는 복지 서비스인지 확인
     *    - 있으면: viewedAt 시간만 업데이트 (중복 저장 방지)
     *    - 없으면: 새로운 기록 추가
     * 3. 사용자의 최근 본 기록이 100개 초과하면 가장 오래된 것 삭제
     *
     * <p>이 데이터는 다음 용도로 사용됩니다:
     * - 개인별 최근 본 복지 서비스 목록 (GET /recent-views)
     * - 전체 사용자 대상 인기 복지 서비스 TOP 10 (GET /recent-views/trending)
     *
     * @param welfareServiceId 복지 서비스 ID
     */
    @Transactional
    public void addRecentView(String welfareServiceId) {
        // 1. 현재 로그인한 사용자 가져오기
        UserJpaEntity user = userFacade.currentUser();
        LocalDateTime now = LocalDateTime.now();

        // 2. 이미 본 적 있는지 확인
        Optional<RecentViewJpaEntity> existingView = recentViewRepository
                .findByUserIdAndWelfareServiceId(user.getId(), welfareServiceId);

        if (existingView.isPresent()) {
            // 2-1. 이미 본 적 있으면 조회 시간만 업데이트
            existingView.get().updateViewedAt(now);
        } else {
            // 2-2. 처음 보는 복지 서비스면 새로 저장
            long count = recentViewRepository.countByUserId(user.getId());

            // 3. 100개 초과 시 가장 오래된 것 삭제
            if (count >= MAX_RECENT_VIEWS) {
                List<RecentViewJpaEntity> oldestViews = recentViewRepository
                        .findByUserIdOrderByViewedAtDesc(user.getId());

                if (!oldestViews.isEmpty()) {
                    // 가장 마지막 = 가장 오래된 것
                    RecentViewJpaEntity oldest = oldestViews.get(oldestViews.size() - 1);
                    recentViewRepository.delete(oldest);
                }
            }

            // 4. 새로운 최근 본 기록 저장
            RecentViewJpaEntity recentView = RecentViewJpaEntity.builder()
                    .userId(user.getId())
                    .welfareServiceId(welfareServiceId)
                    .viewedAt(now)
                    .build();

            recentViewRepository.save(recentView);
        }
    }

    /**
     * 현재 사용자의 최근 본 복지 서비스 목록을 조회합니다.
     *
     * <p>동작 흐름:
     * 1. JWT 토큰에서 현재 로그인한 사용자 정보 추출
     * 2. DB에서 해당 사용자의 최근 본 기록 조회 (최신순)
     * 3. limit 개수만큼만 반환
     *
     * @param limit 조회 개수 (0 이하 또는 100 초과 시 100으로 조정)
     * @return 최근 본 복지 서비스 목록 (최신순)
     */
    @Transactional(readOnly = true)
    public List<RecentViewResponse> getMyRecentViews(int limit) {
        // 1. 현재 로그인한 사용자 가져오기
        UserJpaEntity user = userFacade.currentUser();

        // 2. limit 값 검증 및 조정
        if (limit <= 0 || limit > MAX_RECENT_VIEWS) {
            limit = MAX_RECENT_VIEWS;
        }

        // 3. 최근 본 기록 조회 및 DTO 변환 (QueryDSL 사용)
        return recentViewRepository
                .findByUserIdOrderByViewedAtDesc(user.getId(), limit)
                .stream()
                .map(RecentViewResponse::from)
                .toList();
    }

    /**
     * 현재 사용자의 최근 본 복지 서비스 목록을 조회합니다. (기본값: 100개)
     *
     * @return 최근 본 복지 서비스 목록 (최신순, 최대 100개)
     */
    @Transactional(readOnly = true)
    public List<RecentViewResponse> getMyRecentViews() {
        return getMyRecentViews(MAX_RECENT_VIEWS);
    }

    /**
     * 전체 사용자가 최근에 가장 많이 본 복지 서비스 TOP N을 조회합니다.
     *
     * <p>동작 흐름:
     * 1. 최근 N일간의 데이터만 필터링 (days 파라미터)
     * 2. 복지 서비스 ID별로 그룹핑
     * 3. 각 복지 서비스를 본 고유 사용자 수 집계 (COUNT DISTINCT)
     * 4. 조회수 많은 순으로 정렬하여 상위 limit개 반환
     *
     * <p>사용 예시:
     * - days=7, limit=10: 최근 7일간 가장 인기 있는 복지 서비스 TOP 10
     * - days=30, limit=20: 최근 30일간 가장 인기 있는 복지 서비스 TOP 20
     *
     * @param days 집계 기간 (0 이하 시 기본 7일)
     * @param limit 조회 개수 (0 이하 또는 100 초과 시 10으로 조정)
     * @return 인기 복지 서비스 목록 (조회수 많은 순)
     */
    @Transactional(readOnly = true)
    public List<TrendingWelfareResponse> getTrendingWelfareServices(int days, int limit) {
        // 1. 파라미터 검증 및 조정
        if (days <= 0) {
            days = DEFAULT_TRENDING_DAYS;
        }
        if (limit <= 0 || limit > 100) {
            limit = 10;
        }

        // 2. 집계 시작 시점 계산 (현재 시간 - days)
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        // 3. 인기 복지 서비스 집계 쿼리 실행 (QueryDSL 사용)
        // QueryDSL Projection으로 직접 TrendingWelfareResponse 생성
        return recentViewRepository.findTrendingWelfareServices(since, limit);
    }

    /**
     * 전체 사용자가 최근 7일간 가장 많이 본 복지 서비스 TOP 10을 조회합니다.
     *
     * @return 인기 복지 서비스 TOP 10 (조회수 많은 순)
     */
    @Transactional(readOnly = true)
    public List<TrendingWelfareResponse> getTrendingWelfareServices() {
        return getTrendingWelfareServices(DEFAULT_TRENDING_DAYS, 10);
    }
}
