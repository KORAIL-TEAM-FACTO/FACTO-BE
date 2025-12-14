package team.java.facto_be.domain.chatbot.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import team.java.facto_be.domain.chatbot.service.dto.WebSocketMessage;
import team.java.facto_be.domain.chatbot.service.ChatService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket 연결됨 - sessionId: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            WebSocketMessage.Request request = parseRequest(message);
            log.info("메시지 수신 - sessionId: {}, message: {}", request.getSessionId(), request.getMessage());
            sendMessage(session, WebSocketMessage.Response.start(request.getSessionId()));
            chatService.streamChat(
                    request,
                    content -> sendMessage(session, WebSocketMessage.Response.streaming(request.getSessionId(), content)),
                    () -> sendMessage(session, WebSocketMessage.Response.end(request.getSessionId())),
                    error -> handleStreamingError(session, request.getSessionId(), error)
            );
        } catch (Exception e) {
            handleStreamingError(session, null, e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket 연결 종료 - sessionId: {}, status: {}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 에러 - sessionId: {}", session.getId(), exception);
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage.Response response) {
        try {
            if (session.isOpen()) {
                String json = objectMapper.writeValueAsString(response);
                session.sendMessage(new TextMessage(json));
            }
        } catch (IOException e) {
            log.error("메시지 전송 실패", e);
        }
    }

    private WebSocketMessage.Request parseRequest(TextMessage message) throws IOException {
        return objectMapper.readValue(message.getPayload(), WebSocketMessage.Request.class);
    }

    private void handleStreamingError(WebSocketSession session, String sessionId, Throwable error) {
        log.error("메시지 처리 실패 - sessionId: {}", sessionId, error);
        sendMessage(session, WebSocketMessage.Response.error(sessionId, "메시지 처리 중 오류가 발생했습니다."));
    }
}
