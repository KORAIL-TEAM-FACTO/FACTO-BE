package team.java.facto_be.domain.chatbot.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import team.java.facto_be.global.entity.BaseTimeEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채팅 세션 엔티티
 */
@Entity
@Table(name = "chat_sessions")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatSession extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true, length = 100)
    private String sessionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 200)
    private String title;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity = LocalDateTime.now();

    @Builder.Default
    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    public void updateTitle(String title) {
        this.title = title;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateLastActivity(LocalDateTime time) {
        this.lastActivity = time;
    }
}
