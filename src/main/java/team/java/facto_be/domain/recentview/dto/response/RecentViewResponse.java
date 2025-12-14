package team.java.facto_be.domain.recentview.dto.response;

import team.java.facto_be.domain.recentview.entity.RecentViewJpaEntity;

import java.time.LocalDateTime;

public record RecentViewResponse(
        Long id,
        String welfareServiceId,
        LocalDateTime viewedAt
) {
    public static RecentViewResponse from(RecentViewJpaEntity entity) {
        return new RecentViewResponse(
                entity.getId(),
                entity.getWelfareServiceId(),
                entity.getViewedAt()
        );
    }
}
