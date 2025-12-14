package team.java.facto_be.domain.chatbot.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 질문의 유형을 분류하는 Enum.
 *
 * <p>질문 유형에 따라 다른 응답 전략을 사용합니다.
 */
@Getter
@RequiredArgsConstructor
public enum QueryType {

    /**
     * 탐색형 질문 - 데이터베이스나 시스템 구조에 대한 질문
     * 예: "DB에 welfare_services 테이블이 뭐야?", "어떤 복지 서비스가 있어?"
     */
    DISCOVERY("탐색형",
            "데이터베이스 구조, 테이블 정보, 또는 어떤 복지 서비스가 있는지 탐색하는 질문"),

    /**
     * 주제형 질문 - 특정 주제나 카테고리에 대한 일반적인 질문
     * 예: "청년 주거 혜택 자세히 알려줘", "노인 일자리 지원사업 있어?"
     */
    TOPIC("주제형",
            "생애주기, 대상, 주제 등 특정 카테고리에 대한 복지 서비스를 찾는 질문"),

    /**
     * 서비스 집중형 질문 - 특정 대상이나 구체적인 복지 서비스에 대한 상세 질문
     * 예: "북한이탈주민 취업 지원에 대해 자세히 알려줘", "다문화가정 교육비 지원 신청 방법은?"
     */
    SERVICE_FOCUS("서비스 집중형",
            "특정 대상이나 구체적인 복지 서비스에 대한 상세 정보를 요청하는 질문"),

    /**
     * 일반 대화 - 복지 서비스와 직접 관련없는 일반적인 대화
     * 예: "안녕?", "고마워", "날씨 어때?"
     */
    GENERAL("일반 대화",
            "복지 서비스와 직접 관련없는 일반적인 인사나 대화");

    private final String displayName;
    private final String description;
}
