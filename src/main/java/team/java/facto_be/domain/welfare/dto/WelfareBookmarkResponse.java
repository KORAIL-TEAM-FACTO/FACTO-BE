package team.java.facto_be.domain.welfare.dto;

import java.time.LocalDateTime;

public record WelfareBookmarkResponse(
        String servId,
        String servNm,
        LocalDateTime bookmarkedAt
) { }
