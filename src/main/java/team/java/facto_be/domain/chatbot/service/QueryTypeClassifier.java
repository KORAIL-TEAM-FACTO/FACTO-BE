package team.java.facto_be.domain.chatbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import team.java.facto_be.domain.chatbot.service.enums.QueryType;

import java.util.Map;

/**
 * 사용자 질문의 유형을 AI를 사용하여 분류하는 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryTypeClassifier {

    private final ChatModel chatModel;

    private static final String CLASSIFICATION_PROMPT = """
            다음 사용자 질문을 분석하여 질문 유형을 분류해주세요.

            질문 유형:
            1. DISCOVERY - 데이터베이스 구조, 테이블 정보, 어떤 복지 서비스가 있는지 탐색하는 질문
               예: "DB에 welfare_services 테이블이 뭐야?", "어떤 복지 서비스가 있어?", "복지 종류 알려줘"

            2. TOPIC - 생애주기, 대상, 주제 등 특정 카테고리에 대한 복지 서비스를 찾는 질문
               예: "청년 주거 혜택 자세히 알려줘", "노인 일자리 지원사업 있어?", "장애인 지원금"

            3. SERVICE_FOCUS - 특정 대상이나 구체적인 복지 서비스에 대한 상세 정보를 요청하는 질문
               예: "북한이탈주민 취업 지원에 대해 자세히 알려줘", "다문화가정 교육비 지원 신청 방법은?"

            4. GENERAL - 복지 서비스와 직접 관련없는 일반적인 인사나 대화
               예: "안녕?", "고마워", "날씨 어때?"

            사용자 질문: {userMessage}

            위 질문을 분석하여 다음 중 하나만 응답해주세요: DISCOVERY, TOPIC, SERVICE_FOCUS, GENERAL
            다른 설명 없이 분류 결과만 출력하세요.
            """;

    /**
     * 사용자 질문을 분석하여 QueryType을 반환합니다.
     *
     * @param userMessage 사용자 질문
     * @return 분류된 QueryType
     */
    public QueryType classify(String userMessage) {
        try {
            PromptTemplate template = new PromptTemplate(CLASSIFICATION_PROMPT);
            Prompt prompt = template.create(Map.of("userMessage", userMessage));

            String classification = chatModel.call(prompt)
                    .getResult()
                    .getOutput()
                    .getText()
                    .trim()
                    .toUpperCase();

            QueryType queryType = parseQueryType(classification);
            log.info("질문 분류 - 입력: '{}' → 분류: {}", userMessage, queryType);

            return queryType;

        } catch (Exception e) {
            log.error("질문 분류 실패, TOPIC으로 대체", e);
            return QueryType.TOPIC; // 기본값
        }
    }

    /**
     * 문자열을 QueryType으로 파싱합니다.
     */
    private QueryType parseQueryType(String classification) {
        try {
            return QueryType.valueOf(classification);
        } catch (IllegalArgumentException e) {
            log.warn("알 수 없는 분류 결과: {}, TOPIC으로 대체", classification);
            return QueryType.TOPIC;
        }
    }
}
