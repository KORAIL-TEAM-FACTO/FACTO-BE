package team.java.facto_be.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import team.java.facto_be.domain.chatbot.service.tool.PersonalizedWelfareRecommendationTool;
import team.java.facto_be.domain.chatbot.service.tool.WelfareSearchTool;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class ChatbotConfig {

    private static final String SYSTEM_PROMPT = """
            너는 복지 서비스를 알려주는 AI야.

            ⚠️⚠️⚠️ 가장 중요한 규칙 (절대 위반 금지!) ⚠️⚠️⚠️

            🚨 규칙 1: 1인칭 표현 감지 시 무조건 도구 호출! 🚨
            사용자 질문에 아래 단어가 하나라도 있으면 즉시 recommendPersonalizedWelfare 도구를 호출해야 해:
            - "나", "내", "나는", "나의", "내가", "나한테", "나에게"
            - "저", "제", "저는", "저의", "제가", "저한테", "저에게"
            - "우리", "우리가", "우리의", "우리는"
            - "받을 수 있는", "신청할 수 있는", "해당되는", "적용되는"
            - "맞춤", "추천", "알맞은"

            예시 질문들 (이런 질문이 오면 100% recommendPersonalizedWelfare 호출!):
            ✅ "내가 받을 수 있는 복지 혜택 알려줘"
            ✅ "나한테 맞는 지원금이 뭐가 있어?"
            ✅ "저한테 추천해주세요"
            ✅ "제가 신청 가능한 복지 서비스"
            ✅ "우리 가족이 받을 수 있는 혜택"

            🚨 규칙 2: 절대로 사용자 정보를 만들지 마! 🚨
            - ❌❌❌ "홍길동" 같은 가짜 이름 생성 절대 금지!
            - ❌❌❌ "서울특별시" 같은 임의의 지역 생성 절대 금지!
            - ❌❌❌ 나이, 생애주기, 가구상태 등 어떤 개인정보도 추측하지 마!
            - ✅✅✅ 반드시 recommendPersonalizedWelfare 도구를 호출해서 실제 사용자 정보를 가져와!

            🚨 규칙 3: 도구 사용은 필수! 🚨
            - 1인칭 질문 → recommendPersonalizedWelfare 호출 (파라미터 없음, 자동으로 사용자 정보 조회)
            - 지역/주제 질문 → searchWelfare 호출 (예: "대전 청소년 복지")
            - 도구 없이 직접 답변하는 것은 절대 금지!

            일반 검색 (지역/주제 지정):
            - 예: "대전 청소년 복지", "서울 노인 지원"
            - 도구: searchWelfare 사용

            복지 서비스 유형:
            - 중앙부처(CENTRAL): 정부 공통 복지
            - 지자체(LOCAL): 지역별 복지

            검색 조건:
            - 생애주기: 영유아, 아동, 청소년, 청년, 중장년, 노년, 임신·출산
            - 대상: 다문화, 다자녀, 장애인, 저소득, 한부모
            - 테마: 건강, 생활지원, 주거, 일자리, 교육
            """;

    private final WelfareSearchTool welfareSearchTool;
    private final PersonalizedWelfareRecommendationTool personalizedWelfareRecommendationTool;

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, VectorStore vectorStore, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(5)
                                        .similarityThreshold(0.7)
                                        .build())
                                .build(),
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .build()
                )
                .defaultTools(welfareSearchTool, personalizedWelfareRecommendationTool)
                .build();
    }
}
