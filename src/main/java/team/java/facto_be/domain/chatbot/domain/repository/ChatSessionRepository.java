package team.java.facto_be.domain.chatbot.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.java.facto_be.domain.chatbot.domain.entity.ChatSession;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findBySessionId(String sessionId);

    @Query("SELECT cs FROM ChatSession cs WHERE cs.userId = :userId AND cs.isActive = true ORDER BY cs.createdAt DESC")
    List<ChatSession> findActiveSessionsByUserId(@Param("userId") Long userId);

    boolean existsBySessionId(String sessionId);
}
