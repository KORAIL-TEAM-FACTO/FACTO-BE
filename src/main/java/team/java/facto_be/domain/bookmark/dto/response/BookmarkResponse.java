package team.java.facto_be.domain.bookmark.dto.response;

import team.java.facto_be.domain.bookmark.entity.BookmarkJpaEntity;

import java.time.LocalDateTime;

public record BookmarkResponse(
        Long id,
        String welfareServiceId,
        LocalDateTime createdAt
) {
    public static BookmarkResponse from(BookmarkJpaEntity entity) {
        return new BookmarkResponse(
                entity.getId(),
                entity.getWelfareServiceId(),
                entity.getCreatedAt()
        );
    }
}
