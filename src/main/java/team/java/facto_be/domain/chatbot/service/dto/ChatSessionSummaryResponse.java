package team.java.facto_be.domain.chatbot.service.dto;

import team.java.facto_be.domain.chatbot.domain.entity.ChatSession;

import java.time.LocalDateTime;

public record ChatSessionSummaryResponse(
        Long id,
        String sessionId,
        String title,
        LocalDateTime lastActivity,
        LocalDateTime createdAt
) {
    public static ChatSessionSummaryResponse from(ChatSession s) {
        return new ChatSessionSummaryResponse(
                s.getId(),
                s.getSessionId(),
                s.getTitle(),
                s.getLastActivity(),
                s.getCreatedAt()
        );
    }
}

