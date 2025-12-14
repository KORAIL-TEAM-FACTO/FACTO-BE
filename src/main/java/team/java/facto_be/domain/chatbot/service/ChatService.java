package team.java.facto_be.domain.chatbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.java.facto_be.domain.chatbot.domain.entity.ChatMessage;
import team.java.facto_be.domain.chatbot.domain.entity.ChatSession;
import team.java.facto_be.domain.chatbot.domain.repository.ChatMessageRepository;
import team.java.facto_be.domain.chatbot.domain.repository.ChatSessionRepository;
import team.java.facto_be.domain.chatbot.service.dto.ChatMessageResponse;
import team.java.facto_be.domain.chatbot.service.dto.ChatSessionSummaryResponse;
import team.java.facto_be.domain.chatbot.service.dto.WebSocketMessage;
import team.java.facto_be.domain.chatbot.service.enums.QueryType;
import team.java.facto_be.domain.user.facade.UserFacade;
import team.java.facto_be.domain.welfare.entity.WelfareServiceJpaEntity;
import team.java.facto_be.domain.welfare.repository.WelfareServiceRepository;

import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String DEFAULT_SESSION_TITLE = "새 대화";
    private static final String MEMORY_CONVERSATION_ID = "chat_memory_conversation_id";
    private static final int MIN_TITLE_LENGTH = 10;
    private static final int MAX_TITLE_LENGTH = 30;

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final VectorStore vectorStore;

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final WelfareServiceRepository welfareServiceRepository;

    private final QueryTypeClassifier queryTypeClassifier;
    private final SystemPromptProvider systemPromptProvider;
    private final UserFacade userFacade;

    public record ChatResult(String sessionId, String message, QueryType queryType) {}

    private record ChatContext(ChatSession session, String message) {}

    /**
     * =========================
     * WebSocket 스트리밍 채팅
     * =========================
     */
    @Transactional
    public void streamChat(WebSocketMessage.Request request,
                           Consumer<String> onContent,
                           Runnable onComplete,
                           Consumer<Throwable> onError) {

        try {
            ChatContext context = prepareContext(request.getSessionId(), request.getMessage(), request.getUserId());

            // 질문 유형 분류
            QueryType queryType = queryTypeClassifier.classify(context.message());
            String systemPrompt = systemPromptProvider.getSystemPrompt(queryType);

            log.info("스트리밍 채팅 - 질문 유형: {}", queryType);

            StringBuilder fullResponse = new StringBuilder();

            chatClient.prompt()
                    .system(systemPrompt)
                    .user(context.message())
                    .advisors(advisor -> advisor.param(
                            MEMORY_CONVERSATION_ID,
                            context.session().getSessionId()
                    ))
                    .stream()
                    .content()
                    .doOnNext(chunk -> {
                        fullResponse.append(chunk);
                        onContent.accept(chunk);
                    })
                    .doOnComplete(() -> {
                        saveAssistantMessage(context.session(), fullResponse.toString());
                        onComplete.run();
                    })
                    .doOnError(error -> handleStreamingError(onError, error))
                    .subscribe();

        } catch (Exception e) {
            handleStreamingError(onError, e);
        }
    }

    /**
     * =========================
     * REST API 채팅
     * =========================
     */
    @Transactional
    public ChatResult chat(String sessionId, String message, Long userId) {

        ChatContext context = prepareContext(sessionId, message, userId);

        // 질문 유형 분류
        QueryType queryType = queryTypeClassifier.classify(context.message());
        String systemPrompt = systemPromptProvider.getSystemPrompt(queryType);

        log.info("REST 채팅 - 질문 유형: {}", queryType);

        String response = invokeChatModel(context.session(), context.message(), systemPrompt);
        saveAssistantMessage(context.session(), response);

        return new ChatResult(context.session().getSessionId(), response, queryType);
    }

    /**
     * =========================
     * 세션 조회 또는 생성
     * =========================
     */
    private ChatSession getOrCreateSession(String sessionId, Long userId) {

        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }

        String finalSessionId = sessionId;

        return chatSessionRepository.findBySessionId(sessionId)
                .orElseGet(() -> createSession(finalSessionId, userId));
    }

    private ChatSession createSession(String sessionId, Long userId) {
        return chatSessionRepository.save(
                ChatSession.builder()
                        .sessionId(sessionId)
                        .userId(userId)
                        .title(DEFAULT_SESSION_TITLE)
                        .lastActivity(java.time.LocalDateTime.now())
                        .build()
        );
    }

    /**
     * =========================
     * AI 응답 저장 + 제목 갱신
     * =========================
     */
    private void saveAssistantMessage(ChatSession session, String content) {

        chatMessageRepository.save(
                ChatMessage.assistantMessage(session, content)
        );

        updateLastActivity(session);
        updateSessionTitleIfNeeded(session, content);
    }

    private void updateSessionTitleIfNeeded(ChatSession session, String content) {
        if (!DEFAULT_SESSION_TITLE.equals(session.getTitle()) || content == null) {
            return;
        }

        if (content.length() <= MIN_TITLE_LENGTH) {
            return;
        }

        String title = content.length() > MAX_TITLE_LENGTH
                ? content.substring(0, MAX_TITLE_LENGTH) + "..."
                : content;

        session.updateTitle(title);
        chatSessionRepository.save(session);
    }

    /**
     * =========================
     * 채팅 히스토리 조회
     * =========================
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(String sessionId) {

        ChatSession session = chatSessionRepository
                .findBySessionIdAndUserId(sessionId, userFacade.currentUser().getId())
                .orElseThrow(() -> new IllegalStateException("세션 접근 권한이 없습니다."));

        return chatMessageRepository.findByChatSessionOrderByCreatedAtAsc(session)
                .stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    /**
     * =========================
     * 사용자 세션 목록 조회
     * =========================
     */
    @Transactional(readOnly = true)
    public List<ChatSessionSummaryResponse> getUserSessionSummaries(Long userId) {
        return chatSessionRepository.findActiveSessionsByUserId(userId).stream()
                .map(ChatSessionSummaryResponse::from)
                .toList();
    }

    /**
     * =========================
     * 세션 삭제
     * =========================
     */
    @Transactional
    public void deleteSession(String sessionId) {

        ChatSession session = chatSessionRepository
                .findBySessionIdAndUserId(sessionId, userFacade.currentUser().getId())
                .orElseThrow(() -> new IllegalStateException("세션 접근 권한이 없습니다."));

        chatMemory.clear(sessionId);
        chatMessageRepository.deleteByChatSession(session);
        chatSessionRepository.delete(session);
    }

    /**
     * =========================
     * VectorStore 초기화
     * =========================
     */
    public void initializeVectorStore() {

        log.info("VectorStore 초기화 시작");

        List<WelfareServiceJpaEntity> services =
                welfareServiceRepository.findAll();

        if (services.isEmpty()) {
            log.warn("복지 서비스 데이터가 없습니다.");
            return;
        }

        List<Document> documents = services.stream()
                .map(this::toDocument)
                .toList();

        vectorStore.add(documents);

        log.info("VectorStore 초기화 완료 - {}건", documents.size());
    }

    private ChatContext prepareContext(String sessionId, String message, Long userId) {
        ChatSession session = getOrCreateSession(sessionId, userId);
        chatMessageRepository.save(ChatMessage.userMessage(session, message));
        updateLastActivity(session);
        return new ChatContext(session, message);
    }

    private String invokeChatModel(ChatSession session, String message, String systemPrompt) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .advisors(advisor -> advisor.param(
                        MEMORY_CONVERSATION_ID,
                        session.getSessionId()
                ))
                .call()
                .content();
    }

    private void handleStreamingError(Consumer<Throwable> onError, Throwable error) {
        log.error("스트리밍 채팅 처리 실패", error);
        onError.accept(error);
    }

    private void updateLastActivity(ChatSession session) {
        session.updateLastActivity(java.time.LocalDateTime.now());
        chatSessionRepository.save(session);
    }

    private Document toDocument(WelfareServiceJpaEntity w) {

        StringBuilder content = new StringBuilder();

        content.append("서비스명: ")
                .append(w.getServiceName())
                .append("\n");

        String summary =
                w.getAiSummary() != null
                        ? w.getAiSummary()
                        : w.getServiceSummary();

        if (summary != null) {
            content.append("요약: ")
                    .append(summary)
                    .append("\n");
        }

        if (w.getServiceContent() != null) {
            content.append("내용: ")
                    .append(w.getServiceContent())
                    .append("\n");
        }

        if (w.getCtpvNm() != null) {
            content.append("지역: ")
                    .append(w.getCtpvNm());

            if (w.getSggNm() != null) {
                content.append(" ").append(w.getSggNm());
            }
            content.append("\n");
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("serviceId", w.getServiceId());
        metadata.put("serviceName", w.getServiceName());
        metadata.put("serviceType", w.getServiceType());

        return new Document(content.toString(), metadata);
    }
}
