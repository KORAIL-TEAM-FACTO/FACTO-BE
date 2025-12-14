package team.java.facto_be.domain.chatbot.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WebSocketMessage {

    /**
     * 클라이언트 → 서버 메시지
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String sessionId;
        private String message;
        private Long userId;
    }

    /**
     * 서버 → 클라이언트 메시지
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String sessionId;
        private String content;
        private MessageType type;

        public enum MessageType {
            START,      // 스트리밍 시작
            STREAMING,  // 스트리밍 중
            END,        // 스트리밍 종료
            ERROR       // 에러
        }

        public static Response start(String sessionId) {
            return Response.builder()
                    .sessionId(sessionId)
                    .type(MessageType.START)
                    .build();
        }

        public static Response streaming(String sessionId, String content) {
            return Response.builder()
                    .sessionId(sessionId)
                    .content(content)
                    .type(MessageType.STREAMING)
                    .build();
        }

        public static Response end(String sessionId) {
            return Response.builder()
                    .sessionId(sessionId)
                    .type(MessageType.END)
                    .build();
        }

        public static Response error(String sessionId, String errorMessage) {
            return Response.builder()
                    .sessionId(sessionId)
                    .content(errorMessage)
                    .type(MessageType.ERROR)
                    .build();
        }
    }
}
