package team.java.facto_be.domain.chatbot.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import team.java.facto_be.global.entity.BaseTimeEntity;

/**
 * 채팅 메시지 엔티티
 */
@Entity
@Table(name = "chat_messages")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession chatSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 50)
    private MessageType messageType;

    @Column(name = "sender", nullable = false, length = 100)
    private String sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public enum MessageRole {
        USER, ASSISTANT
    }

    public enum MessageType {
        TEXT
    }

    public static ChatMessage userMessage(ChatSession session, String content) {
        String sender = session.getUserId() != null
                ? session.getUserId().toString()
                : MessageRole.USER.name();

        return ChatMessage.builder()
                .chatSession(session)
                .role(MessageRole.USER)
                .messageType(MessageType.TEXT)
                .sender(sender)
                .content(content)
                .build();
    }

    public static ChatMessage assistantMessage(ChatSession session, String content) {
        return ChatMessage.builder()
                .chatSession(session)
                .role(MessageRole.ASSISTANT)
                .messageType(MessageType.TEXT)
                .sender(MessageRole.ASSISTANT.name())
                .content(content)
                .build();
    }
}
