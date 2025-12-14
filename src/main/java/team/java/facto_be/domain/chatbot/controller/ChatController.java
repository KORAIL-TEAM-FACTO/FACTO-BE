package team.java.facto_be.domain.chatbot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.java.facto_be.domain.chatbot.domain.entity.ChatMessage;
import team.java.facto_be.domain.chatbot.domain.entity.ChatSession;
import team.java.facto_be.domain.chatbot.service.ChatService;
import team.java.facto_be.domain.chatbot.service.dto.ChatMessageResponse;
import team.java.facto_be.domain.chatbot.service.dto.ChatRequest;
import team.java.facto_be.domain.chatbot.service.dto.ChatResponse;
import team.java.facto_be.domain.chatbot.service.dto.ChatSessionSummaryResponse;
import team.java.facto_be.domain.user.facade.UserFacade;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserFacade userFacade;

    /**
     * Non-streaming chat endpoint.
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("POST /api/chat - sessionId: {}, message: {}", request.getSessionId(), request.getMessage());

        Long currentUserId = resolveCurrentUserId();
        ChatService.ChatResult result = chatService.chat(
                request.getSessionId(),
                request.getMessage(),
                currentUserId
        );

        log.info("POST /api/chat - 응답 완료, queryType: {}", result.queryType());

        return ResponseEntity.ok(ChatResponse.builder()
                .sessionId(result.sessionId())
                .message(result.message())
                .queryType(result.queryType())
                .build());
    }

    /**
     * Fetch chat history by sessionId.
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatService.getChatHistory(sessionId));
    }

    /**
     * List sessions for current user.
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionSummaryResponse>> getUserSessions() {
        Long currentUserId = resolveCurrentUserId();
        return ResponseEntity.ok(chatService.getUserSessionSummaries(currentUserId));
    }

    /**
     * Delete session and its messages.
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        log.info("DELETE /api/chat/sessions/{}", sessionId);
        chatService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Admin: initialize vector store.
     */
    @PostMapping("/admin/init-vector-store")
    public ResponseEntity<String> initVectorStore() {
        log.info("POST /api/chat/admin/init-vector-store");
        chatService.initializeVectorStore();
        return ResponseEntity.ok("VectorStore initialized");
    }

    private Long resolveCurrentUserId() {
        try {
            return userFacade.currentUser().getId();
        } catch (Exception e) {
            return null;
        }
    }
}
