package team.java.facto_be.domain.recentview.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import team.java.facto_be.domain.recentview.dto.response.TrendingWelfareResponse;
import team.java.facto_be.domain.recentview.entity.RecentViewJpaEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static team.java.facto_be.domain.recentview.entity.QRecentViewJpaEntity.recentViewJpaEntity;

/**
 * 최근 본 복지 서비스 커스텀 Repository 구현체.
 *
 * <p>QueryDSL을 사용하여 타입 안전하고 가독성 좋은 쿼리를 작성합니다.
 */
@RequiredArgsConstructor
public class RecentViewRepositoryImpl implements RecentViewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RecentViewJpaEntity> findByUserIdOrderByViewedAtDesc(Long userId, int limit) {
        return queryFactory
                .selectFrom(recentViewJpaEntity)
                .where(recentViewJpaEntity.userId.eq(userId))
                .orderBy(recentViewJpaEntity.viewedAt.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<RecentViewJpaEntity> findByUserIdOrderByViewedAtDesc(Long userId) {
        return queryFactory
                .selectFrom(recentViewJpaEntity)
                .where(recentViewJpaEntity.userId.eq(userId))
                .orderBy(recentViewJpaEntity.viewedAt.desc())
                .fetch();
    }

    @Override
    public Optional<RecentViewJpaEntity> findByUserIdAndWelfareServiceId(Long userId, String welfareServiceId) {
        RecentViewJpaEntity result = queryFactory
                .selectFrom(recentViewJpaEntity)
                .where(
                        recentViewJpaEntity.userId.eq(userId),
                        recentViewJpaEntity.welfareServiceId.eq(welfareServiceId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public long countByUserId(Long userId) {
        Long count = queryFactory
                .select(recentViewJpaEntity.count())
                .from(recentViewJpaEntity)
                .where(recentViewJpaEntity.userId.eq(userId))
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public List<TrendingWelfareResponse> findTrendingWelfareServices(LocalDateTime since, int limit) {
        return queryFactory
                .select(Projections.constructor(
                        TrendingWelfareResponse.class,
                        recentViewJpaEntity.welfareServiceId,
                        recentViewJpaEntity.userId.countDistinct()
                ))
                .from(recentViewJpaEntity)
                .where(recentViewJpaEntity.viewedAt.goe(since))
                .groupBy(recentViewJpaEntity.welfareServiceId)
                .orderBy(recentViewJpaEntity.userId.countDistinct().desc())
                .limit(limit)
                .fetch();
    }
}
