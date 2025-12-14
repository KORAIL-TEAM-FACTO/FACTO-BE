package team.java.facto_be.domain.chatbot.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.java.facto_be.domain.chatbot.service.enums.QueryType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String sessionId;
    private String message;
    private QueryType queryType;
}
