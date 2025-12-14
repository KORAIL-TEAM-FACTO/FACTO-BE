package team.java.facto_be.domain.chatbot.service.dto;

import team.java.facto_be.domain.chatbot.domain.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        String role,
        String content,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage m) {
        return new ChatMessageResponse(
                m.getId(),
                m.getRole().name(),
                m.getContent(),
                m.getCreatedAt()
        );
    }
}
