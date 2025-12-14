package team.java.facto_be.domain.chatbot.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.java.facto_be.domain.chatbot.domain.entity.ChatMessage;
import team.java.facto_be.domain.chatbot.domain.entity.ChatSession;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatSessionOrderByCreatedAtAsc(ChatSession chatSession);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.sessionId = :sessionId ORDER BY cm.createdAt DESC LIMIT :limit")
    List<ChatMessage> findRecentMessages(@Param("sessionId") String sessionId, @Param("limit") int limit);

    void deleteByChatSession(ChatSession chatSession);
}
