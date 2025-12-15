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
import team.java.facto_be.domain.chatbot.service.context.UserContextHolder;
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

    private static final int BATCH_SIZE = 50;
    private static final int CHUNK_SIZE = 700;
    private static final int CHUNK_OVERLAP = 100;

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
            // ⚠️ Tool에서 사용자 정보를 조회할 수 있도록 ThreadLocal에 userId 설정
            UserContextHolder.setUserId(request.getUserId());

            ChatContext context = prepareContext(request.getSessionId(), request.getMessage(), request.getUserId());

            // 질문 유형 분류
            QueryType queryType = queryTypeClassifier.classify(context.message());
            String systemPrompt = systemPromptProvider.getSystemPrompt(queryType);

            log.info("스트리밍 채팅 - 질문 유형: {}, userId: {}", queryType, request.getUserId());

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
                    .doFinally(signalType -> {
                        // ⚠️ 반드시 ThreadLocal 정리 (메모리 누수 방지)
                        UserContextHolder.clear();
                    })
                    .subscribe();

        } catch (Exception e) {
            UserContextHolder.clear();  // 예외 발생 시에도 정리
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

        try {
            // ⚠️ Tool에서 사용자 정보를 조회할 수 있도록 ThreadLocal에 userId 설정
            UserContextHolder.setUserId(userId);

            ChatContext context = prepareContext(sessionId, message, userId);

            // 질문 유형 분류
            QueryType queryType = queryTypeClassifier.classify(context.message());
            String systemPrompt = systemPromptProvider.getSystemPrompt(queryType);

            log.info("REST 채팅 - 질문 유형: {}, userId: {}", queryType, userId);

            String response = invokeChatModel(context.session(), context.message(), systemPrompt);
            saveAssistantMessage(context.session(), response);

            return new ChatResult(context.session().getSessionId(), response, queryType);

        } finally {
            // ⚠️ 반드시 ThreadLocal 정리 (메모리 누수 방지)
            UserContextHolder.clear();
        }
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

        // 1. sessionId로 세션 조회
        ChatSession session = chatSessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalStateException("세션을 찾을 수 없습니다."));

        // 2. 현재 사용자 ID 조회 (익명 사용자는 null)
        Long currentUserId = resolveCurrentUserId();
        Long sessionUserId = session.getUserId();

        // 3. 소유권 검증: 세션의 userId와 현재 userId가 일치해야 함
        if (!java.util.Objects.equals(currentUserId, sessionUserId)) {
            log.warn("세션 접근 권한 거부 - sessionId: {}, currentUserId: {}, sessionUserId: {}",
                    sessionId, currentUserId, sessionUserId);
            throw new IllegalStateException("세션 접근 권한이 없습니다.");
        }

        log.info("채팅 히스토리 조회 성공 - sessionId: {}, userId: {}", sessionId, currentUserId);

        return chatMessageRepository.findByChatSessionOrderByCreatedAtAsc(session)
                .stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    /**
     * 현재 사용자 ID를 안전하게 조회합니다.
     * 익명 사용자(비로그인)인 경우 null을 반환합니다.
     */
    private Long resolveCurrentUserId() {
        try {
            return userFacade.currentUser().getId();
        } catch (Exception e) {
            return null;  // 익명 사용자
        }
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

        // 1. sessionId로 세션 조회
        ChatSession session = chatSessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalStateException("세션을 찾을 수 없습니다."));

        // 2. 현재 사용자 ID 조회 (익명 사용자는 null)
        Long currentUserId = resolveCurrentUserId();
        Long sessionUserId = session.getUserId();

        // 3. 소유권 검증: 세션의 userId와 현재 userId가 일치해야 함
        if (!java.util.Objects.equals(currentUserId, sessionUserId)) {
            log.warn("세션 삭제 권한 거부 - sessionId: {}, currentUserId: {}, sessionUserId: {}",
                    sessionId, currentUserId, sessionUserId);
            throw new IllegalStateException("세션 삭제 권한이 없습니다.");
        }

        log.info("세션 삭제 - sessionId: {}, userId: {}", sessionId, currentUserId);

        chatMemory.clear(sessionId);
        chatMessageRepository.deleteByChatSession(session);
        chatSessionRepository.delete(session);
    }

    /**
     * =========================
     * VectorStore 초기화
     * =========================
     * ✅ QuestionAnswerAdvisor가 활성화되어 RAG(Retrieval-Augmented Generation)를 사용합니다.
     *
     * 설정:
     * - similarityThreshold: 0.95 (매우 유사한 문서만 검색)
     * - Tool 결과 우선 사용, RAG는 보조적 컨텍스트 제공
     * - Batch + Chunk 방식으로 효율적 초기화
     *
     * 초기화 후 복지 서비스 데이터가 벡터 임베딩으로 저장되어
     * 의미론적 검색(Semantic Search)이 가능합니다.
     */
    @Transactional(readOnly = true)
    public void initializeVectorStore() {

        log.info("✅ VectorStore 초기화 시작 (Batch + Chunk 적용, RAG 활성화)");

        List<WelfareServiceJpaEntity> services =
                welfareServiceRepository.findAll();

        if (services.isEmpty()) {
            log.warn("복지 서비스 데이터가 없습니다.");
            return;
        }

        List<Document> buffer = new ArrayList<>(BATCH_SIZE);
        int totalChunks = 0;

        for (WelfareServiceJpaEntity service : services) {

            List<Document> docs = toDocuments(service);
            totalChunks += docs.size();

            for (Document doc : docs) {
                buffer.add(doc);

                if (buffer.size() == BATCH_SIZE) {
                    vectorStore.add(buffer);
                    log.info("VectorStore batch add 완료 ({}건)", buffer.size());
                    buffer.clear();
                }
            }
        }

        if (!buffer.isEmpty()) {
            vectorStore.add(buffer);
            log.info("VectorStore 마지막 batch add 완료 ({}건)", buffer.size());
        }

        log.info("VectorStore 초기화 완료 - 서비스 {}건 → 총 Chunk {}건",
                services.size(), totalChunks);
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

    private List<Document> toDocuments(WelfareServiceJpaEntity w) {

        String fullText = buildContent(w);

        List<Document> documents = new ArrayList<>();

        for (int start = 0; start < fullText.length(); start += (CHUNK_SIZE - CHUNK_OVERLAP)) {

            int end = Math.min(start + CHUNK_SIZE, fullText.length());
            String chunk = fullText.substring(start, end);

            Map<String, Object> metadata = buildMetadata(w);
            metadata.put("chunkStart", start);

            documents.add(new Document(chunk, metadata));

            if (end == fullText.length()) {
                break;
            }
        }

        return documents;
    }

    private String buildContent(WelfareServiceJpaEntity w) {

        StringBuilder content = new StringBuilder();

        content.append("서비스명: ").append(w.getServiceName()).append("\n");

        String summary =
                w.getAiSummary() != null
                        ? w.getAiSummary()
                        : w.getServiceSummary();

        if (summary != null && !summary.isBlank()) {
            content.append("요약: ").append(summary).append("\n");
        }

        if (w.getServiceContent() != null && !w.getServiceContent().isBlank()) {
            content.append("내용: ").append(w.getServiceContent()).append("\n");
        }

        if (w.getCtpvNm() != null) {
            content.append("지역: ").append(w.getCtpvNm());
            if (w.getSggNm() != null) {
                content.append(" ").append(w.getSggNm());
            }
            content.append("\n");
        }

        return content.toString();
    }

    private Map<String, Object> buildMetadata(WelfareServiceJpaEntity w) {

        Map<String, Object> metadata = new HashMap<>();

        metadata.put("serviceId", w.getServiceId());
        metadata.put("serviceName", w.getServiceName());
        metadata.put("serviceType", w.getServiceType());

        if (w.getCtpvNm() != null) {
            metadata.put("ctpvNm", w.getCtpvNm());
        }
        if (w.getSggNm() != null) {
            metadata.put("sggNm", w.getSggNm());
        }

        return metadata;
    }
}
