package team.java.facto_be.domain.welfare.dto;

import java.time.LocalDateTime;

public record MostViewedWelfareResponse(
        String servId,
        String servNm,
        Integer viewCount,
        LocalDateTime lastViewedAt
) { }
