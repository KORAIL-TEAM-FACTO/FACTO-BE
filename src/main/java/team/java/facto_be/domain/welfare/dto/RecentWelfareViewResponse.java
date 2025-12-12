package team.java.facto_be.domain.welfare.dto;

import team.java.facto_be.domain.welfare.entity.RecentWelfareView;

import java.time.LocalDateTime;

public record RecentWelfareViewResponse(
        String servId,
        String servNm,
        LocalDateTime viewedAt
) {
    public static RecentWelfareViewResponse fromEntity(RecentWelfareView entity) {
        return new RecentWelfareViewResponse(
                entity.getServId(),
                entity.getServNm(),
                entity.getLastViewedAt()
        );
    }
}
